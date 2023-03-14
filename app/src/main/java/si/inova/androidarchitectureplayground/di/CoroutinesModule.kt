package si.inova.androidarchitectureplayground.di

import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dispatch.core.MainImmediateCoroutineScope
import si.inova.androidarchitectureplayground.common.outcome.CoroutineResourceManager
import si.inova.androidarchitectureplayground.common.reporting.ErrorReporter

@Suppress("unused")
@ContributesTo(ApplicationScope::class)
@Module
class CoroutinesModule {
   @Provides
   fun provideMainCoroutineScope(): MainImmediateCoroutineScope {
      return MainImmediateCoroutineScope()
   }

   @Provides
   fun provideDefaultCoroutineResourceManager(
      mainCoroutineScope: MainImmediateCoroutineScope,
      errorReporter: ErrorReporter
   ): CoroutineResourceManager {
      return CoroutineResourceManager(mainCoroutineScope, errorReporter)
   }
}
