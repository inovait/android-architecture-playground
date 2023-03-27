package si.inova.androidarchitectureplayground.network.interceptors

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import si.inova.androidarchitectureplayground.network.services.HEADER_FORCE_REFRESH

class BypassCacheInterceptor : Interceptor {
   override fun intercept(chain: Interceptor.Chain): Response {
      val forceRefresh = chain.request().header(HEADER_FORCE_REFRESH)?.toBoolean()
      val updatedRequest = if (forceRefresh != null) {
         chain.request().newBuilder()
            .removeHeader(HEADER_FORCE_REFRESH)
            .apply { if (forceRefresh) header("Cache-Control", CacheControl.FORCE_NETWORK.toString()) }
            .build()
      } else {
         chain.request()
      }

      return chain.proceed(updatedRequest)
   }
}
