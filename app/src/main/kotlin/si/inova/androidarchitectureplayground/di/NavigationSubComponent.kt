package si.inova.androidarchitectureplayground.di

import android.content.Intent
import me.tatarka.inject.annotations.Provides
import si.inova.androidarchitectureplayground.MainViewModel
import si.inova.kotlinova.core.time.AndroidDateTimeFormatter
import si.inova.kotlinova.navigation.deeplink.MainDeepLinkHandler
import si.inova.kotlinova.navigation.di.NavigationContext
import si.inova.kotlinova.navigation.di.NavigationInjection
import si.inova.kotlinova.navigation.di.NavigationStackSubComponent
import si.inova.kotlinova.navigation.di.OuterNavigationScope
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesSubcomponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesSubcomponent(OuterNavigationScope::class)
@SingleIn(OuterNavigationScope::class)
interface NavigationSubComponent {
   fun getNavigationInjectionFactory(): NavigationInjection.Factory
   fun getMainDeepLinkHandler(): MainDeepLinkHandler
   fun getNavigationContext(): NavigationContext
   fun getDateFormatter(): AndroidDateTimeFormatter
   fun getMainViewModelFactory(): (Intent) -> MainViewModel

   @Provides
   fun provideNavigationStackSubcomponentFactory(): NavigationStackSubComponent.Factory =
      this as NavigationStackSubComponent.Factory

   @ContributesSubcomponent.Factory(AppScope::class)
   interface Factory {
      fun createNavigationComponent(): NavigationSubComponent
   }
}
