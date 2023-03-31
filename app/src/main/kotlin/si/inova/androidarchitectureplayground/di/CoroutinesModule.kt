package si.inova.androidarchitectureplayground.di

import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dispatch.core.DefaultCoroutineScope
import dispatch.core.MainImmediateCoroutineScope
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.reporting.ErrorReporter

@Suppress("unused")
@ContributesTo(ApplicationScope::class)
@Module
class CoroutinesModule {
   @Provides
   fun provideMainCoroutineScope(): MainImmediateCoroutineScope {
      return MainImmediateCoroutineScope()
   }

   @Provides
   fun provideDefaultCoroutineScope(): DefaultCoroutineScope {
      return DefaultCoroutineScope()
   }

   @Provides
   fun provideDefaultCoroutineResourceManager(
      mainCoroutineScope: MainImmediateCoroutineScope,
      errorReporter: ErrorReporter
   ): CoroutineResourceManager {
      return CoroutineResourceManager(mainCoroutineScope, errorReporter)
   }
}
