package si.inova.androidarchitectureplayground.navigation.instructions

import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.base.NavigationCondition
import si.inova.androidarchitectureplayground.navigation.base.NavigationContext
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey

/**
 * Navigate to [target] if all [conditions] are met.
 *
 * If conditions are not met, Navigator will first navigate to a screen where user can meet the conditions (for example, log-in
 * screen) and then redirect back to the target screen. You can intercept navigation to the condition meeting screen via
 * [conditionScreenWrapper] argument.
 */
@Parcelize
class NavigateWithConditions(
   val target: NavigationInstruction,
   vararg val conditions: NavigationCondition,
   val conditionScreenWrapper: (NavigationInstruction) -> NavigationInstruction = { it }
) :
   NavigationInstruction() {
   override fun performNavigation(backstack: List<ScreenKey>, context: NavigationContext): NavigationResult {
      for (condition in conditions) {
         val overridenTarget = context.mainConditionalNavigationHandler.getNavigationRedirect(
            condition,
            target
         )

         if (overridenTarget != null) {
            return conditionScreenWrapper(overridenTarget).performNavigation(backstack, context)
         }
      }

      return target.performNavigation(backstack, context)
   }

   override fun toString(): String {
      return "NavigateWithConditions(target=$target, conditions=${conditions.contentToString()})"
   }
}
