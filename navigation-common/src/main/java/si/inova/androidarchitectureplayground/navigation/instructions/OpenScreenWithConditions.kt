package si.inova.androidarchitectureplayground.navigation.instructions

import com.zhuinden.simplestack.StateChange
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.Navigator
import si.inova.androidarchitectureplayground.navigation.base.NavigationContext
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.SingleTopKey

@Parcelize
class OpenScreenWithConditions(val screen: ScreenKey) : NavigationInstruction() {
   @OptIn(DelicateCoroutinesApi::class)
   override fun performNavigation(backstack: List<ScreenKey>, context: NavigationContext): NavigationResult {
      for (condition in screen.navigationConditions) {
         val newTarget = context.mainConditionalNavigationHandler.getNavigationRedirect(
            condition,
            OpenScreen(screen)
         )

         if (newTarget != null) {
            return newTarget.performNavigation(backstack, context)
         }
      }

      return openWhileIgnoringConditions(backstack)
   }

   private fun openWhileIgnoringConditions(backstack: List<ScreenKey>): NavigationResult {
      return if (backstack.isNotEmpty() && screen is SingleTopKey && backstack.last().javaClass == screen.javaClass) {
         val newBackstack = backstack.dropLast(1) + screen
         NavigationResult(newBackstack, StateChange.REPLACE)
      } else {
         NavigationResult(backstack + screen, StateChange.FORWARD)
      }
   }

   override fun toString(): String {
      return "OpenScreenWithConditions(screen=$screen)"
   }
}

fun Navigator.navigateTo(screen: ScreenKey) {
   val instruction = if (screen.navigationConditions.isEmpty()) {
      OpenScreen(screen)
   } else {
      OpenScreenWithConditions(screen)
   }

   navigate(instruction)
}
