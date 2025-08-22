package si.inova.androidarchitectureplayground.instrumentation

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import si.inova.androidarchitectureplayground.network.services.BaseServiceFactory

@ContributesTo(AppScope::class)
interface TestNetworkUrlProviders {
   @Provides
   @BaseServiceFactory.BaseUrl
   fun provideBaseUrl(): String {
      return url
   }

   companion object {
      lateinit var url: String
   }
}
