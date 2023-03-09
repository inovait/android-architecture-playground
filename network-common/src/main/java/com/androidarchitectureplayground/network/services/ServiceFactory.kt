package com.androidarchitectureplayground.network.services

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject
import javax.inject.Provider

class ServiceFactory @Inject constructor(
   private val moshi: Moshi,
   private val okHttpClient: Provider<OkHttpClient>
) {
   inline fun <reified S> create(noinline configuration: ServiceCreationScope.() -> Unit = {}): S {
      return create(S::class.java, configuration)
   }

   fun <S> create(klass: Class<S>, configuration: ServiceCreationScope.() -> Unit = {}): S {
      val scope = ServiceCreationScope()
      configuration(scope)

      return Retrofit.Builder()
         .callFactory { okHttpClient.get().newCall(it) }
         .baseUrl("https://dummyjson.com/")
         .addConverterFactory(MoshiConverterFactory.create(moshi))
         .addCallAdapterFactory(ModelResultHandlerCallAdapterFactory(scope.errorHandler))
         .build()
         .create(klass)
   }

   class ServiceCreationScope {
      var errorHandler: ErrorHandler? = null
      // To add later
   }
}
