package si.inova.androidarchitectureplayground.di

import android.app.Application
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dispatch.core.DefaultCoroutineScope
import si.inova.androidarchitectureplayground.MainViewModel
import si.inova.kotlinova.core.reporting.ErrorReporter
import si.inova.kotlinova.core.time.AndroidDateTimeFormatter
import si.inova.kotlinova.navigation.deeplink.MainDeepLinkHandler
import si.inova.kotlinova.navigation.di.NavigationContext
import si.inova.kotlinova.navigation.di.NavigationInjection
import si.inova.kotlinova.navigation.di.OuterNavigationScope

@DependencyGraph(AppScope::class, additionalScopes = [OuterNavigationScope::class])
interface MainApplicationGraph : ApplicationGraph {
   @DependencyGraph.Factory
   interface Factory {
      fun create(
         @Provides
         application: Application,
      ): MainApplicationGraph
   }
}

interface ApplicationGraph {
   fun getErrorReporter(): ErrorReporter
   fun getDefaultCoroutineScope(): DefaultCoroutineScope
   fun getNavigationInjectionFactory(): NavigationInjection.Factory
   fun getMainDeepLinkHandler(): MainDeepLinkHandler
   fun getNavigationContext(): NavigationContext
   fun getDateFormatter(): AndroidDateTimeFormatter
   fun getMainViewModelFactory(): MainViewModel.Factory
}
