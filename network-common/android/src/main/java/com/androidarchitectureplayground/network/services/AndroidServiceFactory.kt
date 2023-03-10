package com.androidarchitectureplayground.network.services

import com.androidarchitectureplayground.network.android.util.GlobalOkHttpDiskCacheManager
import com.squareup.moshi.Moshi
import okhttp3.Cache
import okhttp3.OkHttpClient
import javax.inject.Inject
import javax.inject.Provider

class AndroidServiceFactory @Inject constructor(
   moshi: Moshi,
   okHttpClient: Provider<OkHttpClient>,
   private val cacheManager: GlobalOkHttpDiskCacheManager
) : BaseServiceFactory(moshi, okHttpClient) {
   override fun createCache(): Cache {
      return cacheManager.cache
   }
}
