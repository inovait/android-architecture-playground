package si.inova.androidarchitectureplayground.common.test.datastore

import dispatch.test.TestDispatcherProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlinx.coroutines.test.runTest as nativeRunTest

/**
 * runTest variant that automatically injects test dispatchers as all Dispatch dispatchers.
 *
 * @see kotlinx.coroutines.test.runTest
 */
@OptIn(ExperimentalStdlibApi::class)
fun runTestWithDispatchers(
   context: CoroutineContext = EmptyCoroutineContext,
   dispatchTimeoutMs: Long = 60_000L,
   testBody: suspend TestScope.() -> Unit
) {
   nativeRunTest(context, dispatchTimeoutMs) {
      val dispatcher = requireNotNull(coroutineContext[CoroutineDispatcher]) { "Dispatcher is not set" }

      val newContext = TestDispatcherProvider(dispatcher)

      withContext(newContext) {
         testBody()
      }
   }
}

/**
 * runTest variant that automatically injects test dispatchers as all Dispatch dispatchers.
 *
 * @see kotlinx.coroutines.test.runTest
 */
@OptIn(ExperimentalStdlibApi::class)
fun TestScope.runTestWithDispatchers(
   dispatchTimeoutMs: Long = 60_000L,
   testBody: suspend TestScope.() -> Unit
) {
   this.nativeRunTest(dispatchTimeoutMs) {
      val dispatcher = requireNotNull(coroutineContext[CoroutineDispatcher]) { "Dispatcher is not set" }

      val newContext = TestDispatcherProvider(dispatcher)

      withContext(newContext) {
         testBody()
      }
   }
}
