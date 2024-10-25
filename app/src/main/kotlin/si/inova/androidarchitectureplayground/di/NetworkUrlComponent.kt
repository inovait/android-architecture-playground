package si.inova.androidarchitectureplayground.di

import me.tatarka.inject.annotations.Provides
import si.inova.androidarchitectureplayground.network.services.BaseServiceFactory
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

@ContributesTo(AppScope::class)
interface NetworkUrlComponent {
   @Provides
   @BaseServiceFactory.BaseUrl
   fun provideBaseUrl(): String {
      return "https://dummyjson.com/"
   }
}
