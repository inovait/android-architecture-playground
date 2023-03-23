package si.inova.androidarchitectureplayground.test

import dispatch.core.DispatcherProvider
import dispatch.core.MainImmediateCoroutineScope
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.job
import kotlinx.coroutines.test.TestScope

/**
 * A [DispatcherProvider] that provides test dispatcher for all dispatchers.
 */
@OptIn(ExperimentalStdlibApi::class)
val TestScope.testDispatcherProvider: DispatcherProvider
   get() = SingleDispatcherProvider(requireNotNull(coroutineContext[CoroutineDispatcher]))

/**
 * [MainImmediateCoroutineScope] based on a [TestScope.backgroundScope].
 *
 * This scope will have a test dispatcher and will be automatically cancelled when test finishes.
 */
val TestScope.testMainImmediateBackgroundScope: MainImmediateCoroutineScope
   get() = MainImmediateCoroutineScope(SupervisorJob(backgroundScope.coroutineContext.job), testDispatcherProvider)

/**
 * Dispatcher provider that provides a single dispatcher as every other
 */
class SingleDispatcherProvider(private val dispatcher: CoroutineDispatcher) : DispatcherProvider {
   override val default: CoroutineDispatcher
      get() = dispatcher
   override val io: CoroutineDispatcher
      get() = dispatcher
   override val main: CoroutineDispatcher
      get() = dispatcher
   override val mainImmediate: CoroutineDispatcher
      get() = dispatcher
   override val unconfined: CoroutineDispatcher
      get() = dispatcher
}
