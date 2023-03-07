package com.androidarchitectureplayground.network.services

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

class ServiceFactory @Inject constructor(
   private val moshi: Moshi,
   private val okHttpClient: OkHttpClient
) {
   inline fun <reified S> create(noinline configuration: ServiceCreationScope.() -> Unit = {}): S {
      return create(S::class.java, configuration)
   }

   @Suppress("UnusedPrivateMember") // Unused for now
   fun <S> create(klass: Class<S>, configuration: ServiceCreationScope.() -> Unit = {}): S {
      return Retrofit.Builder()
         .client(okHttpClient)
         .baseUrl("https://dummyjson.com/")
         .addConverterFactory(MoshiConverterFactory.create(moshi))
         .build()
         .create(klass)
   }

   class ServiceCreationScope {
      // To add later
   }
}
