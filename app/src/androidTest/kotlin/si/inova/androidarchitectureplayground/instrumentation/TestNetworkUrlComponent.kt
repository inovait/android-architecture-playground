package si.inova.androidarchitectureplayground.instrumentation

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import si.inova.androidarchitectureplayground.network.services.BaseServiceFactory
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

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
