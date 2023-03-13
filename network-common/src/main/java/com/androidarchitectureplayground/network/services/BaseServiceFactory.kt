package com.androidarchitectureplayground.network.services

import com.squareup.moshi.Moshi
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import si.inova.androidarchitectureplayground.common.reporting.ErrorReporter
import si.inova.androidarchitectureplayground.common.time.TimeProvider
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Qualifier

open class BaseServiceFactory @Inject constructor(
   private val moshi: Moshi,
   private val okHttpClient: Provider<OkHttpClient>,
   private val errorReporter: ErrorReporter,
   private val timeProvider: TimeProvider,
   @BaseUrl
   private val baseUrl: String
) : ServiceFactory {
   override fun <S> create(klass: Class<S>, configuration: ServiceFactory.ServiceCreationScope.() -> Unit): S {
      val scope = ServiceFactory.ServiceCreationScope()
      configuration(scope)

      val updatedClient = lazy {
         okHttpClient.get().newBuilder()
            .apply {
               if (scope.cache) {
                  createCache()?.let { cache(it) }
               }
            }
            .apply {
               scope.okHttpCustomizer?.let { it() }
            }
            .build()
      }

      return Retrofit.Builder()
         .callFactory { updatedClient.value.newCall(it) }
         .baseUrl(baseUrl)
         .addConverterFactory(MoshiConverterFactory.create(moshi))
         .addCallAdapterFactory(StaleWhileRevalidateCallAdapterFactory(scope.errorHandler, errorReporter, timeProvider))
         .addCallAdapterFactory(SuspendCallAdapterFactory(scope.errorHandler))
         .build()
         .create(klass)
   }

   open fun createCache(): Cache? {
      return null
   }

   @Qualifier
   annotation class BaseUrl
}
