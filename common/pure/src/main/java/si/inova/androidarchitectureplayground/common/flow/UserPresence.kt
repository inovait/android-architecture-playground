package si.inova.androidarchitectureplayground.common.flow

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlin.coroutines.CoroutineContext

/**
 * Only collect upstream flow when user is present and is actively using the app. This can be used to, for example, only activate
 * GPS location when app is open, to conserve battery life.
 *
 * You can use optional [onStartWithUserNotPresent] parameter, which mimics [Flow.onStart] and will get invoked whenever this flow
 * starts getting collected without user present. You can emit some sort of default value in that block to ensure flow does not
 * get stuck until user comes back.
 */
fun <T> Flow<T>.onlyFlowWhenUserPresent(onStartWithUserNotPresent: suspend FlowCollector<T>.() -> Unit = {}): Flow<T> {
   return flow {
      val presenceProvider = currentCoroutineContext()[UserPresenceProvider]
         ?: error("This flow needs UserPresenceProvider from upstream to function")
      var startedWithoutPresence = true

      val dataFlow = presenceProvider.isUserPresentFlow().flatMapLatest { userPresent ->
         if (userPresent) {
            startedWithoutPresence = false
            this@onlyFlowWhenUserPresent
         } else {
            if (startedWithoutPresence) {
               startedWithoutPresence = false
               flow(onStartWithUserNotPresent)
            } else {
               emptyFlow()
            }
         }
      }

      emitAll(dataFlow)
   }
}

fun UserPresenceProvider(sharedFlow: MutableSharedFlow<*>): UserPresenceProvider {
   return UserPresenceProvider { sharedFlow.hasActiveSubscribersFlow() }
}

fun interface UserPresenceProvider : CoroutineContext.Element {
   override val key: CoroutineContext.Key<*>
      get() = UserPresenceProvider

   fun isUserPresentFlow(): Flow<Boolean>

   companion object : CoroutineContext.Key<UserPresenceProvider>
}
