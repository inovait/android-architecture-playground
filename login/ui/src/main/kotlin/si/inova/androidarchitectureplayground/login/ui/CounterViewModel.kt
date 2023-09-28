package si.inova.androidarchitectureplayground.login.ui

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.services.CoroutineScopedService
import javax.inject.Inject

class CounterViewModel @Inject constructor(
   private val resources: CoroutineResourceManager,
) : CoroutineScopedService(resources.scope) {
   private val _counterText = MutableStateFlow<Outcome<String>>(Outcome.Success(""))
   val counterText: StateFlow<Outcome<String>> = _counterText

   val paused = MutableStateFlow<Boolean>(false)

   fun startCounting() = resources.launchResourceControlTask(_counterText) {
   }

   fun setPaused(paused: Boolean) {
      this.paused.value = paused
   }
}
