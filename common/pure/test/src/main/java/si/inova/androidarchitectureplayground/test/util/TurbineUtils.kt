package si.inova.androidarchitectureplayground.test.util

import app.cash.turbine.ReceiveTurbine
import app.cash.turbine.test
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlin.coroutines.coroutineContext
import kotlin.time.Duration

/**
 * Start turbine test without eating exceptions.
 *
 * This is a workaround for the https://github.com/cashapp/turbine/issues/165
 *
 * @see Flow.test
 */
suspend fun <T> Flow<T>.testWithExceptions(
   timeout: Duration? = null,
   name: String? = null,
   validate: suspend ReceiveTurbine<T>.() -> Unit,
) {
   val coroutineExceptionHandler =
      requireNotNull(coroutineContext[CoroutineExceptionHandler]) {
         "testWithException should only be called inside runTest scope"
      }

   return this.catch {
      coroutineExceptionHandler.handleException(coroutineContext, it)
   }.test(timeout, name, validate)
}
