package si.inova.androidarchitectureplayground.di

import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import si.inova.androidarchitectureplayground.network.services.BaseServiceFactory

@Suppress("unused")
@ContributesTo(ApplicationScope::class)
@Module
class NetworkUrlModule {
   @Provides
   @BaseServiceFactory.BaseUrl
   fun provideBaseUrl(): String {
      return "https://dummyjson.com/"
   }
}
