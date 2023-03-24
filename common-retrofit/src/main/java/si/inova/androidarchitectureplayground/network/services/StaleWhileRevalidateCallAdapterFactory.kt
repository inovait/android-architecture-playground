package si.inova.androidarchitectureplayground.network.services

import dispatch.core.withDefault
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import okhttp3.CacheControl
import okhttp3.Request
import okhttp3.internal.cache.CacheStrategy
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import retrofit2.parseResponse
import si.inova.androidarchitectureplayground.common.exceptions.UnknownCauseException
import si.inova.androidarchitectureplayground.common.outcome.CauseException
import si.inova.androidarchitectureplayground.common.outcome.Outcome
import si.inova.androidarchitectureplayground.common.outcome.catchIntoOutcome
import si.inova.androidarchitectureplayground.common.reporting.ErrorReporter
import si.inova.androidarchitectureplayground.common.time.TimeProvider
import si.inova.androidarchitectureplayground.network.util.enqueueAndAwait
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.net.HttpURLConnection
import java.util.concurrent.TimeUnit

/**
 * Call adapter factory that handles Flow<Outcome<T>> return types.
 *
 * When Retrofit function returns Flow<Outcome<T>>, stale while revalidate strategy will be used. First, [Outcome.Progress] will
 * be emitted with stale cached data, while new fresh data will be loaded from the server. Afterwards, [Outcome.Success] will
 * be emitted with the new fresh server data.
 */
class StaleWhileRevalidateCallAdapterFactory(
   private val errorHandler: ErrorHandler?,
   private val errorReporter: ErrorReporter,
   private val timeProvider: TimeProvider
) : CallAdapter.Factory() {
   override fun get(returnType: Type, annotations: Array<out Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
      if (returnType !is ParameterizedType) {
         return null
      }

      if (getRawType(returnType) != Flow::class.java) {
         return null
      }

      val firstNestedType = getParameterUpperBound(0, returnType)
      if (firstNestedType !is ParameterizedType) {
         return null
      }

      if (getRawType(firstNestedType) != Outcome::class.java) {
         return null
      }

      val secondNestedType = getParameterUpperBound(0, firstNestedType)
      return ResultAdapter<Any>(secondNestedType, retrofit, timeProvider)
   }

   private inner class ResultAdapter<T>(
      private val responseType: Type,
      private val retrofit: Retrofit,
      private val timeProvider: TimeProvider
   ) : CallAdapter<T, Flow<Outcome<T>>> {
      @DelicateCoroutinesApi
      override fun adapt(call: Call<T>): Flow<Outcome<T>> {
         // Perform enqueue on the background thread to ensure OkHttp initialization does not block the main thread
         return channelFlow {
            withDefault {
               val (networkRequest, dataFromCache) = makeCacheRequest(call)
               networkRequest?.let { makeMainRequest(it, call, dataFromCache) }
            }
         }
      }

      private suspend fun ProducerScope<Outcome<T>>.makeCacheRequest(originalCall: Call<T>): Pair<Request?, T?> {
         var networkRequest: Request? = originalCall.request()

         return try {
            val forceNetwork = originalCall.request().header(HEADER_FORCE_REFRESH)?.toBoolean() ?: false

            val cacheRequest = originalCall.request().newBuilder()
               .removeHeader(HEADER_FORCE_REFRESH)
               .cacheControl(CacheControl.FORCE_CACHE)
               .build()

            val rawCacheResponse = retrofit.callFactory().newCall(cacheRequest).enqueueAndAwait()

            if (rawCacheResponse.code == HttpURLConnection.HTTP_GATEWAY_TIMEOUT) {
               // OkHttp returns gateway timeout when there is no cache or cache is not valid
               return originalCall.request() to null
            }

            val responseWithAddedCacheParameters = if (forceNetwork) {
               rawCacheResponse.newBuilder()
                  .header("Cache-Control", CacheControl.Builder().maxAge(1, TimeUnit.MILLISECONDS).build().toString())
                  .build()
            } else {
               rawCacheResponse
            }

            val strategy = CacheStrategy.Factory(
               timeProvider.currentTimeMillis(),
               originalCall.request(),
               responseWithAddedCacheParameters
            ).compute()

            networkRequest = strategy.networkRequest?.newBuilder()?.removeHeader(HEADER_FORCE_REFRESH)?.build()

            val cacheResponse = strategy.cacheResponse
            if (networkRequest == null && cacheResponse == null) {
               send(
                  Outcome.Error(
                     UnknownCauseException(
                        message = "Stale while revalidate call to ${cacheRequest.url} failed" +
                           " - both cache and network are null"
                     )
                  )
               )
               return null to null
            }

            if (cacheResponse == null) {
               return originalCall.request() to null
            }

            val parsedResponse = originalCall.parseResponse(cacheResponse)

            val result = catchIntoOutcome {
               val data = parsedResponse.bodyOrThrow(errorHandler)

               if (networkRequest != null) {
                  Outcome.Progress()
               } else {
                  Outcome.Success(data)
               }
            }

            if (result is Outcome.Error) {
               handleCacheError(networkRequest, result.exception, originalCall)
            } else {
               send(result)
            }

            networkRequest to result.data
         } catch (e: Exception) {
            handleCacheError(networkRequest, e.transformRetrofitException(originalCall.request().url), originalCall)
            networkRequest to null
         }
      }

      private suspend fun ProducerScope<Outcome<T>>.handleCacheError(
         networkRequest: Request?,
         e: CauseException,
         originalCall: Call<T>
      ) {
         if (networkRequest == null) {
            // Cache is our main request - forward all errors
            send(Outcome.Error(e))
         } else {
            // Do not surface cache errors to the user, user will receive a new result anyway. Just report it
            // so we can fix it
            errorReporter.report(Exception("Cache call to ${originalCall.request().url} failed", e))
         }
      }

      private suspend fun ProducerScope<Outcome<T>>.makeMainRequest(
         networkRequest: Request,
         call: Call<T>,
         dataFromCache: T?
      ) {
         try {
            val networkResponse = retrofit.callFactory().newCall(networkRequest).enqueueAndAwait()
            val parsedResponse = call.parseResponse(networkResponse)

            val result = catchIntoOutcome {
               Outcome.Success(parsedResponse.bodyOrThrow(errorHandler))
            }

            send(result)
         } catch (e: Exception) {
            send(Outcome.Error(e.transformRetrofitException(networkRequest.url), dataFromCache))
         }
      }

      override fun responseType(): Type = responseType
   }
}

const val HEADER_FORCE_REFRESH = "Force-Refresh-From-Network"
