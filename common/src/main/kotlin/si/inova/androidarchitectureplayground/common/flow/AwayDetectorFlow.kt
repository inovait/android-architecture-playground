package si.inova.androidarchitectureplayground.common.flow

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import si.inova.kotlinova.core.flow.UserPresenceProvider
import si.inova.kotlinova.core.time.DefaultTimeProvider
import si.inova.kotlinova.core.time.TimeProvider
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

/**
 * A flow that will detect when user has been away for at least [time] (15 minutes by default). If user leaves the screen and
 * then comes back after this tome, flow will emit *true*. Otherwise, it will emit *false*.
 *
 * This requires [UserPresenceProvider] to be installed on current coroutine context.
 */
@Suppress("FunctionNaming") // This is a factory function
fun AwayDetectorFlow(time: Duration = 15.minutes, timeProvider: TimeProvider = DefaultTimeProvider): Flow<Boolean> {
   return flow {
      val presenceProvider = currentCoroutineContext()[UserPresenceProvider]
         ?: error("This flow needs UserPresenceProvider from upstream to function")

      emit(false)

      while (currentCoroutineContext().isActive) {
         presenceProvider.isUserPresentFlow().first { !it }
         val leaveTime = timeProvider.currentMonotonicTimeMillis()

         presenceProvider.isUserPresentFlow().first { it }
         val returnedTime = timeProvider.currentMonotonicTimeMillis()

         if ((returnedTime - leaveTime) >= time.inWholeMilliseconds) {
            emit(true)
         }
      }
   }
}
