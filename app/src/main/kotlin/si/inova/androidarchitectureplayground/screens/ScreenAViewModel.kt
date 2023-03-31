package si.inova.androidarchitectureplayground.screens

import android.util.Log
import androidx.compose.runtime.Stable
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import si.inova.androidarchitectureplayground.navigation.base.SingleScreenViewModel
import si.inova.androidarchitectureplayground.navigation.keys.ScreenAKey
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.core.time.TimeProvider
import javax.inject.Inject

@Stable
class ScreenAViewModel @Inject constructor(
   private val resources: CoroutineResourceManager,
   private val timeProvider: TimeProvider,
) : SingleScreenViewModel<ScreenAKey>(resources.scope) {
   private val _result = MutableStateFlow<Outcome<Int>>(Outcome.Progress())
   val result: StateFlow<Outcome<Int>> = _result

   var currentTimeMillis = 0L
   override fun onServiceRegistered() {
      Log.d("ViewModel", "got key $key")
   }

   fun doATask() {
      @Suppress("MagicNumber") // Just a demo
      resources.launchResourceControlTask(_result) {
         delay(1_000)
         value = Outcome.Success(3)
         currentTimeMillis = timeProvider.currentTimeMillis()
      }
   }
}
