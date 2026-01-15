package si.inova.androidarchitectureplayground.navigation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.zhuinden.simplestack.Backstack
import si.inova.kotlinova.navigation.screenkeys.ScreenKey

@Composable
fun Backstack.historyAsState(): State<List<ScreenKey>> {
   val historyState = remember { mutableStateOf<List<ScreenKey>>(getHistory<ScreenKey>()) }

   DisposableEffect(this) {
      val listener = Backstack.CompletionListener {
         historyState.value = getHistory<ScreenKey>()
      }

      addStateChangeCompletionListener(listener)

      onDispose {
         removeStateChangeCompletionListener(listener)
      }
   }

   return historyState
}
