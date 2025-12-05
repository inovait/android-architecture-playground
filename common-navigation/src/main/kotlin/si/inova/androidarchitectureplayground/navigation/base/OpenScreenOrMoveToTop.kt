
package si.inova.androidarchitectureplayground.navigation.base

import com.zhuinden.simplestack.StateChange
import kotlinx.parcelize.Parcelize
import si.inova.kotlinova.navigation.di.NavigationContext
import si.inova.kotlinova.navigation.instructions.NavigationInstruction
import si.inova.kotlinova.navigation.navigator.Navigator
import si.inova.kotlinova.navigation.screenkeys.ScreenKey
import si.inova.kotlinova.navigation.screenkeys.SingleTopKey

/**
 * If the screen already exists on the backstack, it moves it to the top. Otherwise, it adds it to the backstack.
 */
@Parcelize
data class OpenScreenOrReplaceExistingType(val screen: ScreenKey) : NavigationInstruction() {
   override fun performNavigation(backstack: List<ScreenKey>, context: NavigationContext): NavigationResult {
      println("check $backstack")
      return if (backstack.isNotEmpty() && backstack.last().javaClass == screen.javaClass) {
         println("remove")
         val newBackstack = backstack.dropLast(1) + screen
         NavigationResult(newBackstack, StateChange.REPLACE).also { println("res $it") }
      } else {
         val backstackWithoutTargetScreen = backstack.filter { it != screen }
         NavigationResult(backstackWithoutTargetScreen + screen, StateChange.FORWARD)
      }
   }
}

/**
 * If the provided screen already exists on the backstack, it moves the existing screen in the backstack it to the top.
 * Otherwise, it opens it on normally.
 *
 * If screen has any [ScreenKey.navigationConditions], they will be navigated to if they are not met.
 */

fun Navigator.navigateToOrReplace(screen: ScreenKey) {
   navigate(OpenScreenOrReplaceExistingType(screen))
}
