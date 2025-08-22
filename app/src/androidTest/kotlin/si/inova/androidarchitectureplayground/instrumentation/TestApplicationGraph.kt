package si.inova.androidarchitectureplayground.instrumentation

import android.app.Application
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import si.inova.androidarchitectureplayground.di.ApplicationGraph
import si.inova.androidarchitectureplayground.di.ErrorReportingProviders
import si.inova.androidarchitectureplayground.di.NetworkUrlProviders
import si.inova.kotlinova.navigation.di.OuterNavigationScope

@DependencyGraph(
   AppScope::class,
   isExtendable = true,
   additionalScopes = [OuterNavigationScope::class],
   excludes = [ErrorReportingProviders::class, NetworkUrlProviders::class]
)
interface TestApplicationGraph : ApplicationGraph {
   @DependencyGraph.Factory
   interface Factory {
      fun create(
         @Provides
         application: Application,
      ): TestApplicationGraph
   }
}
