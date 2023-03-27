package si.inova.androidarchitectureplayground.instrumentation

import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import si.inova.androidarchitectureplayground.di.NetworkUrlModule
import si.inova.androidarchitectureplayground.network.services.BaseServiceFactory

@Suppress("unused")
@ContributesTo(ApplicationScope::class, replaces = [NetworkUrlModule::class])
@Module
object TestNetworkUrlModule {
   lateinit var url: String

   @Provides
   @BaseServiceFactory.BaseUrl
   fun provideBaseUrl(): String {
      return url
   }
}
