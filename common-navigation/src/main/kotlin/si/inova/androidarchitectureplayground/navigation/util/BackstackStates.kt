package si.inova.androidarchitectureplayground.navigation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.History
import si.inova.kotlinova.navigation.screenkeys.ScreenKey

@Composable
fun Backstack.historyAsState(): State<History<ScreenKey>> {
   val historyState = remember { mutableStateOf(getHistory<ScreenKey>()) }

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
