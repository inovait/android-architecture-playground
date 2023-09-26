package si.inova.androidarchitectureplayground.login.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.selects.onTimeout
import kotlinx.coroutines.selects.select
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.services.CoroutineScopedService
import javax.inject.Inject

class CounterViewModel @Inject constructor(
   private val resources: CoroutineResourceManager,
) : CoroutineScopedService(resources.scope) {
   private val _counter = MutableStateFlow<Outcome<Int>>(Outcome.Success(0))
   val counter: StateFlow<Outcome<Int>> = _counter

   val paused = MutableStateFlow<Boolean>(false)

   fun startCounting() = resources.launchResourceControlTask(_counter) {
      var enabled = true
      val pausedChannel = paused.produceIn(this)

      for (i in 10 downTo 0) {
         while (true) {
            val emitted = select<Boolean> {
               pausedChannel.onReceive { paused ->
                  enabled = !paused
                  false
               }

               if (enabled) {
                  onTimeout(500) {
                     emit(Outcome.Success(i))
                     true
                  }
               }
            }

            if (emitted) {
               break
            }
         }
      }
   }

   fun setPaused(paused: Boolean) {
      this.paused.value = paused
   }
}
