package si.inova.androidarchitectureplayground.navigation.instructions

import com.zhuinden.simplestack.StateChange
import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.base.NavigationContext
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey

@Parcelize
class MultiNavigationInstructions(
   vararg val instructions: NavigationInstruction,
   val overrideDirection: Int? = null
) : NavigationInstruction() {
   override fun performNavigation(backstack: List<ScreenKey>, context: NavigationContext): NavigationResult {
      var lastDirection = StateChange.REPLACE
      var currentBackstack = backstack

      for (instruction in instructions) {
         val res = instruction.performNavigation(currentBackstack, context)
         lastDirection = res.direction
         currentBackstack = res.newBackstack
      }

      return NavigationResult(currentBackstack, overrideDirection ?: lastDirection)
   }

   override fun toString(): String {
      return "MultiNavigationInstructions(instructions=${instructions.contentToString()}, " +
         "overrideDirection=${overrideDirection ?: "null"})"
   }
}
