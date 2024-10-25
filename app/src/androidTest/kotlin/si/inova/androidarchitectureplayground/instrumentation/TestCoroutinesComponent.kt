package si.inova.androidarchitectureplayground.instrumentation

import dispatch.core.DefaultCoroutineScope
import dispatch.core.DispatcherProvider
import dispatch.core.IOCoroutineScope
import dispatch.core.MainImmediateCoroutineScope
import me.tatarka.inject.annotations.Provides
import si.inova.kotlinova.compose.androidtest.idlingresource.FixedIdlingDispatcher
import si.inova.kotlinova.compose.androidtest.idlingresource.RegisteringCoroutineResourceManager
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.reporting.ErrorReporter
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

@ContributesTo(AppScope::class)
interface TestCoroutinesComponent {
   @Provides
   fun provideMainCoroutineScope(): MainImmediateCoroutineScope {
      return MainImmediateCoroutineScope(dispatcherProvider = idlingDispatcherProvider)
   }

   @Provides
   fun provideDefaultCoroutineScope(): DefaultCoroutineScope {
      return DefaultCoroutineScope(dispatcherProvider = idlingDispatcherProvider)
   }

   @Provides
   fun provideIOCoroutineScope(): IOCoroutineScope {
      return IOCoroutineScope(dispatcherProvider = idlingDispatcherProvider)
   }

   @Provides
   fun provideDefaultCoroutineResourceManager(
      mainCoroutineScope: MainImmediateCoroutineScope,
      errorReporter: ErrorReporter,
   ): CoroutineResourceManager {
      return RegisteringCoroutineResourceManager(mainCoroutineScope, errorReporter)
   }

   companion object {
      private val defaultDispatcherProvider = object : DispatcherProvider {}

      val idlingDispatcherProvider = object : DispatcherProvider {
         override val default = FixedIdlingDispatcher(defaultDispatcherProvider.default)
         override val io = FixedIdlingDispatcher(defaultDispatcherProvider.io)
         override val main = FixedIdlingDispatcher(defaultDispatcherProvider.main)
         override val mainImmediate = FixedIdlingDispatcher(defaultDispatcherProvider.mainImmediate)
         override val unconfined = FixedIdlingDispatcher(defaultDispatcherProvider.unconfined)
      }
   }
}
