package si.inova.androidarchitectureplayground.di

import dispatch.core.DefaultCoroutineScope
import dispatch.core.DispatcherProvider
import dispatch.core.IOCoroutineScope
import dispatch.core.MainImmediateCoroutineScope
import dev.zacsweers.metro.Provides
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.reporting.ErrorReporter
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo

@ContributesTo(AppScope::class)
interface CoroutinesProviders {
   @Provides
   fun provideMainCoroutineScope(): MainImmediateCoroutineScope {
      return MainImmediateCoroutineScope(object : DispatcherProvider {})
   }

   @Provides
   fun provideDefaultCoroutineScope(): DefaultCoroutineScope {
      return DefaultCoroutineScope(object : DispatcherProvider {})
   }

   @Provides
   fun provideIOCoroutineScope(): IOCoroutineScope {
      return IOCoroutineScope(object : DispatcherProvider {})
   }

   @Provides
   fun provideDefaultCoroutineResourceManager(
      mainCoroutineScope: MainImmediateCoroutineScope,
      errorReporter: ErrorReporter,
   ): CoroutineResourceManager {
      return CoroutineResourceManager(mainCoroutineScope, errorReporter)
   }
}
