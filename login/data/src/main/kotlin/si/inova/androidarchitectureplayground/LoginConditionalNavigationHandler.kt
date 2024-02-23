package si.inova.androidarchitectureplayground

import com.squareup.anvil.annotations.ContributesMultibinding
import dagger.multibindings.ClassKey
import si.inova.androidarchitectureplayground.common.di.ApplicationScope
import si.inova.androidarchitectureplayground.login.LoginRepository
import si.inova.androidarchitectureplayground.navigation.conditions.UserLoggedIn
import si.inova.androidarchitectureplayground.navigation.keys.LoginScreenKey
import si.inova.kotlinova.navigation.conditions.ConditionalNavigationHandler
import si.inova.kotlinova.navigation.conditions.NavigationCondition
import si.inova.kotlinova.navigation.instructions.NavigationInstruction
import si.inova.kotlinova.navigation.instructions.OpenScreen
import javax.inject.Inject

@ContributesMultibinding(ApplicationScope::class)
@ClassKey(UserLoggedIn::class)
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
