package si.inova.androidarchitectureplayground.network.di

import com.appmattus.certificatetransparency.cache.DiskCache
import com.appmattus.certificatetransparency.certificateTransparencyInterceptor
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import okhttp3.OkHttpClient
import si.inova.kotlinova.retrofit.interceptors.BypassCacheInterceptor
import java.time.Duration

@ContributesTo(AppScope::class)
interface NetworkProviders {
   // Uncomment when adding adapters
   // val moshiAdapters: Set<MoshiAdapter>

   @Provides
   @SingleIn(AppScope::class)
   fun provideMoshi(
      adapters: Set<@JvmSuppressWildcards MoshiAdapter> = emptySet(), // Remove empty set when adding adapters
   ): Moshi {
      if (Thread.currentThread().name == "main") {
         error("Moshi should not be initialized on the main thread")
      }

      return createMoshi(adapters)
   }

   @Provides
   @SingleIn(AppScope::class)
   fun provideOkHttpClient(
      certificateTransparencyDiskCache: DiskCache,
   ): OkHttpClient {
      if (Thread.currentThread().name == "main") {
         error("OkHttp should not be initialized on the main thread")
      }

      return prepareDefaultOkHttpClient(certificateTransparencyDiskCache).build()
   }

   companion object {
      fun prepareDefaultOkHttpClient(certificateTransparencyDiskCache: DiskCache? = null): OkHttpClient.Builder {
         return OkHttpClient.Builder()
            .addInterceptor(BypassCacheInterceptor())
            .addNetworkInterceptor(
               certificateTransparencyInterceptor {
                  diskCache = certificateTransparencyDiskCache
               }
            )
            .callTimeout(DEFAULT_TIMEOUT)
            .readTimeout(DEFAULT_TIMEOUT)
            .writeTimeout(DEFAULT_TIMEOUT)
            .connectTimeout(DEFAULT_TIMEOUT)
      }

      fun createMoshi(
         adapters: Set<@JvmSuppressWildcards MoshiAdapter> = emptySet(), // Remove empty set when adding adapters
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
   }
}

interface MoshiAdapter

private val DEFAULT_TIMEOUT = Duration.ofSeconds(10)
