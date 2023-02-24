package si.inova.androidarchitectureplayground

import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesMultibinding
import dagger.multibindings.ClassKey
import si.inova.androidarchitectureplayground.navigation.base.ConditionalNavigationHandler
import si.inova.androidarchitectureplayground.navigation.base.NavigationCondition
import si.inova.androidarchitectureplayground.navigation.conditions.UserLoggedIn
import si.inova.androidarchitectureplayground.navigation.instructions.NavigationInstruction
import si.inova.androidarchitectureplayground.navigation.instructions.OpenScreen
import si.inova.androidarchitectureplayground.navigation.keys.LoginScreenKey
import javax.inject.Inject

@ContributesMultibinding(ApplicationScope::class)
@ClassKey(UserLoggedIn::class)
class LoginConditionalNavigationHandler @Inject constructor(
   private val loginRepository: LoginRepository
) : ConditionalNavigationHandler {
   override fun getNavigationRedirect(
      condition: NavigationCondition,
      navigateToIfConditionMet: NavigationInstruction
   ): NavigationInstruction {
      return if (loginRepository.isLoggedIn) {
         navigateToIfConditionMet
      } else {
         OpenScreen(LoginScreenKey(navigateToIfConditionMet))
      }
   }
}
