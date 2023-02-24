package si.inova.androidarchitectureplayground.navigation.instructions

import com.zhuinden.simplestack.StateChange
import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.Navigator
import si.inova.androidarchitectureplayground.navigation.base.NavigationContext
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey

@Parcelize
object GoBack : NavigationInstruction() {
   override fun performNavigation(backstack: List<ScreenKey>, context: NavigationContext): NavigationResult {
      return if (backstack.size > 1) {
         NavigationResult(backstack.dropLast(1), StateChange.BACKWARD)
      } else {
         NavigationResult(backstack, StateChange.REPLACE)
      }
   }
}

fun Navigator.goBack() {
   navigate(GoBack)
}
