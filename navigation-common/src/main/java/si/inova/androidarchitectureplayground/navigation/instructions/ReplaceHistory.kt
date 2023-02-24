package si.inova.androidarchitectureplayground.navigation.instructions

import com.zhuinden.simplestack.StateChange
import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey

@Parcelize
class ReplaceHistory(vararg val history: ScreenKey) : NavigationInstruction() {
   override fun performNavigation(backstack: List<ScreenKey>): NavigationResult {
      return NavigationResult(history.toList(), StateChange.FORWARD)
   }
}
