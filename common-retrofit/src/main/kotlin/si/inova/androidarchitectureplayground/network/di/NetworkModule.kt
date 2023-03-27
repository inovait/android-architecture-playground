package si.inova.androidarchitectureplayground.network.di

import com.squareup.anvil.annotations.ContributesTo
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.multibindings.Multibinds
import okhttp3.OkHttpClient
import si.inova.androidarchitectureplayground.common.PureApplicationScope
import si.inova.androidarchitectureplayground.network.interceptors.BypassCacheInterceptor
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
      fun provideOkHttpClient(): OkHttpClient {
         if (Thread.currentThread().name == "main") {
            error("OkHttp should not be initialized on the main thread")
         }

         return prepareDefaultOkHttpClient().build()
      }

      fun prepareDefaultOkHttpClient(): OkHttpClient.Builder {
         return OkHttpClient.Builder()
            .addInterceptor(BypassCacheInterceptor())
      }
   }
}

interface MoshiAdapter
