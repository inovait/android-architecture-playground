package si.inova.androidarchitectureplayground.network.services

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import si.inova.androidarchitectureplayground.common.outcome.CauseException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * A [CallAdapter.Factory] that will throw proper [CauseException] exceptions on all retrofit calls
 *
 */
class SuspendCallAdapterFactory(
   private val errorHandler: ErrorHandler? = null
) : CallAdapter.Factory() {
   override fun get(
      returnType: Type,
      annotations: Array<Annotation>,
      retrofit: Retrofit
   ): CallAdapter<*, *>? {
      if (returnType !is ParameterizedType) {
         return null
      }

      if (getRawType(returnType) != Call::class.java) {
         return null
      }

      val actualNestedTypeToDecode = getParameterUpperBound(0, returnType)

      return ResultAdapter<Any>((actualNestedTypeToDecode))
   }

   private inner class ResultAdapter<T>(private val responseType: Type) : CallAdapter<T, Call<T>> {
      override fun adapt(call: Call<T>): Call<T> = ResultCall(call)
      override fun responseType(): Type = responseType
   }

   private inner class ResultCall<T>(proxy: Call<T>) : CallDelegate<T, T>(proxy) {
      @OptIn(DelicateCoroutinesApi::class)
      override fun enqueueImpl(callback: Callback<T>) {
         // Perform enqueue on the background thread to ensure OkHttp initialization does not block the main thread
         GlobalScope.launch {
            proxy.enqueue(object : Callback<T> {
               override fun onFailure(call: Call<T>, t: Throwable) {
                  callback.onFailure(call, t.transformRetrofitException(request().url))
               }

               override fun onResponse(call: Call<T>, response: Response<T>) {
                  return try {
                     val result = response.bodyOrThrow(errorHandler)
                     callback.onResponse(call, Response.success(result))
                  } catch (e: Exception) {
                     callback.onFailure(call, e.transformRetrofitException(request().url))
                  }
               }
            })
         }
      }

      override fun cloneImpl(): Call<T> = ResultCall(proxy.clone())

      override fun executeImpl(): Response<T> {
         val response = proxy.execute()
         if (!response.isSuccessful) {
            throw errorHandler?.generateExceptionFromErrorBody(response, response.createParentException())
               ?: response.createParentException()
         }

         return Response.success(response.bodyOrThrow(errorHandler))
      }

      override fun timeout(): Timeout = proxy.timeout()
   }

   private abstract class CallDelegate<TIn, TOut>(
      protected val proxy: Call<TIn>
   ) : Call<TOut> {
      override fun execute(): Response<TOut> = executeImpl()
      final override fun enqueue(callback: Callback<TOut>) = enqueueImpl(callback)
      final override fun clone(): Call<TOut> = cloneImpl()

      override fun cancel() = proxy.cancel()
      override fun request(): Request = proxy.request()
      override fun isExecuted() = proxy.isExecuted
      override fun isCanceled() = proxy.isCanceled

      abstract fun enqueueImpl(callback: Callback<TOut>)
      abstract fun cloneImpl(): Call<TOut>
      abstract fun executeImpl(): Response<TOut>
   }
}
