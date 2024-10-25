package si.inova.androidarchitectureplayground

import me.tatarka.inject.annotations.Inject
import si.inova.androidarchitectureplayground.login.LoginRepository
import si.inova.androidarchitectureplayground.navigation.keys.LoginScreenKey
import si.inova.kotlinova.navigation.conditions.ConditionalNavigationHandler
import si.inova.kotlinova.navigation.conditions.NavigationCondition
import si.inova.kotlinova.navigation.instructions.NavigationInstruction
import si.inova.kotlinova.navigation.instructions.OpenScreen

class LoginConditionalNavigationHandler @Inject constructor(
   private val loginRepository: LoginRepository,
) : ConditionalNavigationHandler {
   override fun getNavigationRedirect(
      condition: NavigationCondition,
      navigateToIfConditionMet: NavigationInstruction,
   ): NavigationInstruction {
      return if (loginRepository.isLoggedIn) {
         navigateToIfConditionMet
      } else {
         OpenScreen(LoginScreenKey(navigateToIfConditionMet))
      }
   }
}
