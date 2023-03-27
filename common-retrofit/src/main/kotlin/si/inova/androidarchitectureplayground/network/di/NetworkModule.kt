package si.inova.androidarchitectureplayground.network.di

import com.squareup.anvil.annotations.ContributesTo
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import si.inova.androidarchitectureplayground.common.PureApplicationScope
import si.inova.androidarchitectureplayground.network.interceptors.BypassCacheInterceptor
import javax.inject.Singleton

@Module
@ContributesTo(PureApplicationScope::class)
object NetworkModule {
   @Provides
   @Singleton
   fun provideMoshi(): Moshi {
      if (Thread.currentThread().name == "main") {
         error("Moshi should not be initialized on the main thread")
      }

      return Moshi.Builder().build()
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
