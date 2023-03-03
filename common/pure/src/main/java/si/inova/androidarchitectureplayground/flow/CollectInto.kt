package si.inova.androidarchitectureplayground.flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch

/**
 * Collect this flow and forward all emissions into provided [target].
 *
 * This method suspends until flow is completed.
 */
suspend fun <T> Flow<T>.collectInto(target: MutableSharedFlow<in T>) {
   target.emitAll(this)
}

/**
 * Launch a new job that will collect this flow and forward all emissions into provided [target].
 *
 * Job finishes once this flow is completed.
 */
fun <T> Flow<T>.launchAndCollectInto(scope: CoroutineScope, target: MutableSharedFlow<in T>): Job {
   return scope.launch {
      collectInto(target)
   }
}
