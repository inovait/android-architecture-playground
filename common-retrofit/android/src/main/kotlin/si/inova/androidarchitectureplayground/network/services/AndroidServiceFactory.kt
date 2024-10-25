package si.inova.androidarchitectureplayground.network.services

import com.squareup.moshi.Moshi
import dispatch.core.DefaultCoroutineScope
import me.tatarka.inject.annotations.Inject
import okhttp3.Cache
import okhttp3.OkHttpClient
import si.inova.androidarchitectureplayground.network.exceptions.DefaultErrorHandler
import si.inova.kotlinova.core.reporting.ErrorReporter
import si.inova.kotlinova.retrofit.caching.GlobalOkHttpDiskCacheManager

class AndroidServiceFactory @Inject constructor(
   moshi: () -> Moshi,
   errorReporter: ErrorReporter,
   okHttpClient: () -> OkHttpClient,
   defaultCoroutineScope: DefaultCoroutineScope,
   defaultErrorHandler: DefaultErrorHandler,
   @BaseUrl
   baseUrl: String,
   private val cacheManager: GlobalOkHttpDiskCacheManager,
) : BaseServiceFactory(defaultCoroutineScope, moshi, okHttpClient, errorReporter, defaultErrorHandler, baseUrl) {
   override fun createCache(): Cache {
      return cacheManager.cache
   }
}
