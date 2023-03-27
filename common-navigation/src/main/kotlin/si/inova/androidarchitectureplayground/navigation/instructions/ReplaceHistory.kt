package si.inova.androidarchitectureplayground.navigation.instructions

import com.zhuinden.simplestack.StateChange
import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.base.NavigationContext
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey

@Parcelize
class ReplaceHistory(vararg val history: ScreenKey) : NavigationInstruction() {
   override fun performNavigation(backstack: List<ScreenKey>, context: NavigationContext): NavigationResult {
      return NavigationResult(history.toList(), StateChange.FORWARD)
   }

   override fun toString(): String {
      return "ReplaceHistory(history=${history.contentToString()})"
   }
}
