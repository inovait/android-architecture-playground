package com.androidarchitectureplayground.network.di

import com.squareup.anvil.annotations.ContributesTo
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import si.inova.androidarchitectureplayground.common.PureApplicationScope
import javax.inject.Singleton

@Module
@ContributesTo(PureApplicationScope::class)
class NetworkModule {
   @Provides
   @Singleton
   fun provideMoshi(): Moshi {
      return Moshi.Builder().build()
   }

   @Provides
   @Singleton
   fun provideOkHttpClient(): OkHttpClient {
      return OkHttpClient()
   }
}
