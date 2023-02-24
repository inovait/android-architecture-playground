package si.inova.androidarchitectureplayground.navigation.instructions

import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.base.NavigationCondition
import si.inova.androidarchitectureplayground.navigation.base.NavigationContext
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey

@Parcelize
class NavigateWithConditions(val target: NavigationInstruction, vararg val conditions: NavigationCondition) :
   NavigationInstruction() {
   override fun performNavigation(backstack: List<ScreenKey>, context: NavigationContext): NavigationResult {
      for (condition in conditions) {
         val overridenTarget = context.mainConditionalNavigationHandler.getNavigationRedirect(
            condition,
            target
         )

         if (overridenTarget != null) {
            return overridenTarget.performNavigation(backstack, context)
         }
      }

      return target.performNavigation(backstack, context)
   }

   override fun toString(): String {
      return "NavigateWithConditions(target=$target, conditions=${conditions.contentToString()})"
   }
}
