package si.inova.androidarchitectureplayground.navigation.instructions

import com.zhuinden.simplestack.StateChange
import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.Navigator
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.SingleTopKey

@Parcelize
class OpenScreen(val screen: ScreenKey) : NavigationInstruction() {
   override fun performNavigation(backstack: List<ScreenKey>): NavigationResult {
      return if (backstack.isNotEmpty() && screen is SingleTopKey && backstack.last().javaClass == screen.javaClass) {
         val newBackstack = backstack.dropLast(1) + screen
         NavigationResult(newBackstack, StateChange.REPLACE)
      } else {
         NavigationResult(backstack + screen, StateChange.FORWARD)
      }
   }
}

fun Navigator.navigateTo(screen: ScreenKey) {
   navigate(OpenScreen(screen))
}
