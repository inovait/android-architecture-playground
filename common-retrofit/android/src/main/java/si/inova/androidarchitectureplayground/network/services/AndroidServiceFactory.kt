package si.inova.androidarchitectureplayground.network.services

import com.squareup.moshi.Moshi
import dispatch.core.DefaultCoroutineScope
import okhttp3.Cache
import okhttp3.OkHttpClient
import si.inova.androidarchitectureplayground.common.reporting.ErrorReporter
import si.inova.androidarchitectureplayground.common.time.TimeProvider
import si.inova.androidarchitectureplayground.network.android.util.GlobalOkHttpDiskCacheManager
import javax.inject.Inject
import javax.inject.Provider

class AndroidServiceFactory @Inject constructor(
   moshi: Provider<Moshi>,
   errorReporter: ErrorReporter,
   timeProvider: TimeProvider,
   okHttpClient: Provider<OkHttpClient>,
   defaultCoroutineScope: DefaultCoroutineScope,
   @BaseUrl
   baseUrl: String,
   private val cacheManager: GlobalOkHttpDiskCacheManager
) : BaseServiceFactory(defaultCoroutineScope, moshi, okHttpClient, errorReporter, timeProvider, baseUrl) {
   override fun createCache(): Cache {
      return cacheManager.cache
   }
}
