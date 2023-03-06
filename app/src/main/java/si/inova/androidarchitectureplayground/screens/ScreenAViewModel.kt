package si.inova.androidarchitectureplayground.screens

import android.util.Log
import androidx.compose.runtime.Stable
import dispatch.core.MainImmediateCoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import si.inova.androidarchitectureplayground.navigation.base.SingleScreenViewModel
import si.inova.androidarchitectureplayground.navigation.keys.ScreenAKey
import javax.inject.Inject

@Stable
class ScreenAViewModel @Inject constructor(
   scope: MainImmediateCoroutineScope,
) : SingleScreenViewModel<ScreenAKey>(scope) {
   var result = 0
   override fun onServiceRegistered() {
      Log.d("ViewModel", "got key $key")
   }

   fun doATask() {
      coroutineScope.launch {
         @Suppress("MagicNumber") // Just a demo
         delay(1_000)
         result = 2
      }
   }
}
