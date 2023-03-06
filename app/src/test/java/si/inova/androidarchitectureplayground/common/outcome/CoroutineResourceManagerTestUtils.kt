package si.inova.androidarchitectureplayground.common.outcome

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.test.TestScope

/**
 * Return [CoroutineResourceManager] that runs with test dispatcher and
 * reports all unknown exceptions to the test coroutine runner.
 */
fun TestScope.testCoroutineResourceManagerTest(): CoroutineResourceManager {
   val coroutineExceptionHandler = requireNotNull(coroutineContext[CoroutineExceptionHandler])

   return CoroutineResourceManager(
      backgroundScope
   ) { coroutineExceptionHandler.handleException(this@testCoroutineResourceManagerTest.coroutineContext, it) }
}
