package si.inova.androidarchitectureplayground.navigation.base

import si.inova.androidarchitectureplayground.navigation.instructions.NavigationInstruction

interface ConditionalNavigationHandler {
   /**
    * Return *null* if user meets passed condition. Otherwise, return a new [NavigationInstruction] that
    * will be navigated to where user can meet precondition (for example, a login screen). Once user meets precondition
    * on that screen, it should navigate to the [navigateToIfConditionMet].
    */
   fun getNavigationRedirect(
      condition: NavigationCondition,
      navigateToIfConditionMet: NavigationInstruction
   ): NavigationInstruction?
}
