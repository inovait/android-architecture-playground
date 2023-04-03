package si.inova.androidarchitectureplayground.network.di

import com.appmattus.certificatetransparency.cache.DiskCache
import com.appmattus.certificatetransparency.certificateTransparencyInterceptor
import com.squareup.anvil.annotations.ContributesTo
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.multibindings.Multibinds
import okhttp3.OkHttpClient
import si.inova.androidarchitectureplayground.network.services.BaseServiceFactory
import si.inova.kotlinova.core.di.PureApplicationScope
import si.inova.kotlinova.retrofit.interceptors.BypassCacheInterceptor
import javax.inject.Singleton

@Module
@ContributesTo(PureApplicationScope::class)
abstract class NetworkModule {
   @Multibinds
   abstract fun multibindsMoshiAdapters(): Set<@JvmSuppressWildcards MoshiAdapter>

   companion object {
      @Provides
      @Singleton
      fun provideMoshi(
         adapters: Set<@JvmSuppressWildcards MoshiAdapter>
      ): Moshi {
         if (Thread.currentThread().name == "main") {
            error("Moshi should not be initialized on the main thread")
         }

         return Moshi.Builder().also {
            for (adapter in adapters) {
               if (adapter is JsonAdapter.Factory) {
                  it.addLast(adapter)
               } else {
                  it.addLast(adapter)
               }
            }
         }.build()
      }

      @Provides
      @Singleton
      fun provideOkHttpClient(
         @BaseServiceFactory.BaseUrl
         baseUrl: String,
         certificateTransparencyDiskCache: DiskCache?
      ): OkHttpClient {
         if (Thread.currentThread().name == "main") {
            error("OkHttp should not be initialized on the main thread")
         }

         return prepareDefaultOkHttpClient(baseUrl, certificateTransparencyDiskCache).build()
      }

      fun prepareDefaultOkHttpClient(
         baseUrl: String = "https://dummyjson.com/",
         certificateTransparencyDiskCache: DiskCache? = null
      ): OkHttpClient.Builder {
         return OkHttpClient.Builder()
            .addInterceptor(BypassCacheInterceptor())
            .addNetworkInterceptor(certificateTransparencyInterceptor {
               -"*.*"

               +baseUrl

               diskCache = certificateTransparencyDiskCache
            })
      }
   }
}

interface MoshiAdapter
