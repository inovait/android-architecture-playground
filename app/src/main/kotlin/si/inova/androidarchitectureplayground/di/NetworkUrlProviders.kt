package si.inova.androidarchitectureplayground.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import si.inova.androidarchitectureplayground.network.services.BaseServiceFactory

@ContributesTo(AppScope::class)
interface NetworkUrlProviders {
   @Provides
   @BaseServiceFactory.BaseUrl
   fun provideBaseUrl(): String {
      return "https://dummyjson.com/"
   }
}
