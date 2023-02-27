package si.inova.androidarchitectureplayground.di

import android.app.Application
import android.content.Context
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import dagger.multibindings.Multibinds
import si.inova.androidarchitectureplayground.navigation.base.ConditionalNavigationHandler
import si.inova.androidarchitectureplayground.navigation.base.DeepLinkHandler

@Suppress("unused")
@ContributesTo(ApplicationScope::class)
@Module(subcomponents = [NavigationStackComponent::class])
abstract class AppModule {
   @Binds
   abstract fun Application.bindToContext(): Context

   @Multibinds
   abstract fun provideDeepLinkHandlers(): Set<@JvmSuppressWildcards DeepLinkHandler>

   @Multibinds
   abstract fun provideConditionalNavigationHandlers():
      Map<@JvmSuppressWildcards Class<*>, @JvmSuppressWildcards ConditionalNavigationHandler>
}
