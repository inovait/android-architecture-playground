package si.inova.androidarchitectureplayground.common.flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Collect this flow and forward all emissions into provided [target].
 * Upstream flows will get access to the [onlyFlowWhenUserPresent], where user is considered as present if [target] has any
 * collectors.
 *
 * This method suspends until flow is completed.
 */
suspend fun <T> Flow<T>.collectInto(target: MutableSharedFlow<in T>) {
   val userPresenceProvider = UserPresenceProvider(target)
   withContext(userPresenceProvider) {
      target.emitAll(this@collectInto)
   }
}

/**
 * Launch a new job that will collect this flow and forward all emissions into provided [target].
 * Upstream flows will get access to the [onlyFlowWhenUserPresent], where user is considered as present if [target] has any
 * collectors.
 *
 * Job finishes once this flow is completed.
 */
fun <T> Flow<T>.launchAndCollectInto(scope: CoroutineScope, target: MutableSharedFlow<in T>): Job {
   return scope.launch {
      collectInto(target)
   }
}
