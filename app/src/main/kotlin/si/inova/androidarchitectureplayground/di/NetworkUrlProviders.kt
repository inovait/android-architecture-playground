package si.inova.androidarchitectureplayground.di

import dev.zacsweers.metro.Provides
import si.inova.androidarchitectureplayground.network.services.BaseServiceFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo

@ContributesTo(AppScope::class)
interface NetworkUrlProviders {
   @Provides
   @BaseServiceFactory.BaseUrl
   fun provideBaseUrl(): String {
      return "https://dummyjson.com/"
   }
}
