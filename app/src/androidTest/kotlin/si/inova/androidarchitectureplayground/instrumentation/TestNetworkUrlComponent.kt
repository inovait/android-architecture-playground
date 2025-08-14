package si.inova.androidarchitectureplayground.instrumentation

import dev.zacsweers.metro.Component
import dev.zacsweers.metro.Provides
import si.inova.androidarchitectureplayground.network.services.BaseServiceFactory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo

@ContributesTo(AppScope::class)
@Component
interface TestNetworkUrlComponent {
   @Provides
   @BaseServiceFactory.BaseUrl
   fun provideBaseUrl(): String {
      return url
   }

   companion object {
      lateinit var url: String
   }
}
