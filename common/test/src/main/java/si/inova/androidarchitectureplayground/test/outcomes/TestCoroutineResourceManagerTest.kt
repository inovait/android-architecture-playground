package si.inova.androidarchitectureplayground.test.outcomes

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.test.TestScope
import si.inova.androidarchitectureplayground.common.outcome.CoroutineResourceManager
import si.inova.androidarchitectureplayground.common.reporting.ErrorReporter

/**
 * Return [CoroutineResourceManager] that runs with test dispatcher and
 * reports all unknown exceptions to the test coroutine runner.
 */
fun TestScope.testCoroutineResourceManagerTest(): CoroutineResourceManager {
   return CoroutineResourceManager(backgroundScope, throwingErrorReporter())
}

/**
 * ErrorReporter that will throw all reported exceptions at the end of the test
 */
fun TestScope.throwingErrorReporter(): ErrorReporter {
   val coroutineExceptionHandler = requireNotNull(coroutineContext[CoroutineExceptionHandler])
   return ErrorReporter { coroutineExceptionHandler.handleException(coroutineContext, it) }
}
