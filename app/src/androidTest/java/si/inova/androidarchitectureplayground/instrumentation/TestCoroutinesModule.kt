package si.inova.androidarchitectureplayground.instrumentation

import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dispatch.core.DefaultDispatcherProvider
import dispatch.core.DispatcherProvider
import dispatch.core.MainImmediateCoroutineScope
import si.inova.androidarchitectureplayground.common.outcome.CoroutineResourceManager
import si.inova.androidarchitectureplayground.common.reporting.ErrorReporter
import si.inova.androidarchitectureplayground.di.CoroutinesModule
import si.inova.androidarchitectureplayground.util.IdlingDispatcher
import si.inova.androidarchitectureplayground.util.RegisteringCoroutineResourceManager

@Suppress("unused")
@ContributesTo(ApplicationScope::class, replaces = [CoroutinesModule::class])
@Module
object TestCoroutinesModule {
   val dispatcherProvider = object : DispatcherProvider {
      override val default = IdlingDispatcher(DefaultDispatcherProvider.get().default)
      override val io = IdlingDispatcher(DefaultDispatcherProvider.get().io)
      override val main = IdlingDispatcher(DefaultDispatcherProvider.get().main)
      override val mainImmediate = IdlingDispatcher(DefaultDispatcherProvider.get().mainImmediate)
      override val unconfined = IdlingDispatcher(DefaultDispatcherProvider.get().unconfined)
   }

   @Provides
   fun provideMainCoroutineScope(): MainImmediateCoroutineScope {
      return MainImmediateCoroutineScope(dispatcherProvider = dispatcherProvider)
   }

   @Provides
   fun provideDefaultCoroutineResourceManager(
      mainCoroutineScope: MainImmediateCoroutineScope,
      errorReporter: ErrorReporter
   ): CoroutineResourceManager {
      return RegisteringCoroutineResourceManager(mainCoroutineScope, errorReporter)
   }
}
