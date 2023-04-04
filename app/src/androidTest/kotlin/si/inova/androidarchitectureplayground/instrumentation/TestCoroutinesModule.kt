package si.inova.androidarchitectureplayground.instrumentation

import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dispatch.core.DefaultCoroutineScope
import dispatch.core.DefaultDispatcherProvider
import dispatch.core.DispatcherProvider
import dispatch.core.MainImmediateCoroutineScope
import si.inova.androidarchitectureplayground.di.CoroutinesModule
import si.inova.kotlinova.compose.androidtest.idlingresource.FixedIdlingDispatcher
import si.inova.kotlinova.compose.androidtest.idlingresource.RegisteringCoroutineResourceManager
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.reporting.ErrorReporter

@Suppress("unused")
@ContributesTo(ApplicationScope::class, replaces = [CoroutinesModule::class])
@Module
object TestCoroutinesModule {
   val dispatcherProvider = object : DispatcherProvider {
      override val default = FixedIdlingDispatcher(DefaultDispatcherProvider.get().default)
      override val io = FixedIdlingDispatcher(DefaultDispatcherProvider.get().io)
      override val main = FixedIdlingDispatcher(DefaultDispatcherProvider.get().main)
      override val mainImmediate = FixedIdlingDispatcher(DefaultDispatcherProvider.get().mainImmediate)
      override val unconfined = FixedIdlingDispatcher(DefaultDispatcherProvider.get().unconfined)
   }

   @Provides
   fun provideMainCoroutineScope(): MainImmediateCoroutineScope {
      return MainImmediateCoroutineScope(dispatcherProvider = dispatcherProvider)
   }

   @Provides
   fun provideDefaultCoroutineScope(): DefaultCoroutineScope {
      return DefaultCoroutineScope(dispatcherProvider = dispatcherProvider)
   }

   @Provides
   fun provideDefaultCoroutineResourceManager(
      mainCoroutineScope: MainImmediateCoroutineScope,
      errorReporter: ErrorReporter
   ): CoroutineResourceManager {
      return RegisteringCoroutineResourceManager(mainCoroutineScope, errorReporter)
   }
}
