package si.inova.androidarchitectureplayground.navigation.instructions

import com.zhuinden.simplestack.StateChange
import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.base.NavigationContext
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey

/**
 * @param direction The direction of the [StateChange]: [StateChange.BACKWARD], [StateChange.FORWARD] or [StateChange.REPLACE].
 */
@Parcelize
data class ReplaceCurrentScreen(val screen: ScreenKey, val direction: Int = StateChange.REPLACE) : NavigationInstruction() {
   override fun performNavigation(backstack: List<ScreenKey>, context: NavigationContext): NavigationResult {
      return NavigationResult(backstack.dropLast(1) + backstack, direction)
   }
}
