package si.inova.androidarchitectureplayground.simplestack

import si.inova.androidarchitectureplayground.navigation.base.ConditionalNavigationHandler
import si.inova.androidarchitectureplayground.navigation.base.NavigationCondition
import si.inova.androidarchitectureplayground.navigation.instructions.NavigationInstruction
import javax.inject.Inject
import javax.inject.Provider

class MainConditionalNavigationHandler @Inject constructor(
   private val handlers: Map<
      @JvmSuppressWildcards Class<*>,
      @JvmSuppressWildcards Provider<ConditionalNavigationHandler>
      >
) : ConditionalNavigationHandler {
   override fun getNavigationRedirect(
      condition: NavigationCondition,
      navigateToIfConditionMet: NavigationInstruction
   ): NavigationInstruction? {
      val handler = handlers[condition.javaClass]?.get()
         ?: error(
            "Failed to find condition handler for condition ${condition.javaClass.name}. " +
               "Did you add @ContributesMultibinding annotation to it?"
         )

      return handler.getNavigationRedirect(condition, navigateToIfConditionMet)
   }
}
