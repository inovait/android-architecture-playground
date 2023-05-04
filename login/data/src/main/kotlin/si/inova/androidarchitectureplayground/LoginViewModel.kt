package si.inova.androidarchitectureplayground

import androidx.compose.runtime.Stable
import dispatch.core.MainImmediateCoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import si.inova.androidarchitectureplayground.login.LoginRepository
import si.inova.androidarchitectureplayground.navigation.keys.LoginScreenKey
import si.inova.kotlinova.navigation.instructions.GoBack
import si.inova.kotlinova.navigation.instructions.MultiNavigationInstructions
import si.inova.kotlinova.navigation.navigator.Navigator
import si.inova.kotlinova.navigation.services.SingleScreenViewModel
import javax.inject.Inject

@Stable
class LoginViewModel @Inject constructor(
   private val loginRepository: LoginRepository,
   private val navigator: Navigator,
   scope: MainImmediateCoroutineScope,
) : SingleScreenViewModel<LoginScreenKey>(scope) {
   private val _isLoggedIn = MutableStateFlow(false)
   val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

   init {
      coroutineScope.launch {
         loginRepository.isLoggedInFlow().collect {
            _isLoggedIn.value = it
         }
      }
   }

   fun setLoggedIn(loggedIn: Boolean) {
      loginRepository.setLoggedIn(loggedIn)
   }

   fun finishLogin() {
      navigator.navigate(
         MultiNavigationInstructions(
            GoBack,
            key.target
         )
      )
   }
}
