package si.inova.androidarchitectureplayground.network.services

import okhttp3.OkHttpClient
import si.inova.kotlinova.retrofit.callfactory.ErrorHandler

interface ServiceFactory {
   fun <S> create(klass: Class<S>, configuration: ServiceCreationScope.() -> Unit = {}): S
   class ServiceCreationScope {
      var errorHandler: ErrorHandler? = null
      var okHttpCustomizer: (OkHttpClient.Builder.() -> Unit)? = null

      var cache: Boolean = true
   }
}

inline fun <reified S> ServiceFactory.create(noinline configuration: ServiceFactory.ServiceCreationScope.() -> Unit = {}): S {
   return create(S::class.java, configuration)
}

fun ServiceFactory.ServiceCreationScope.okHttp(block: OkHttpClient.Builder.() -> Unit) {
   okHttpCustomizer = block
}
