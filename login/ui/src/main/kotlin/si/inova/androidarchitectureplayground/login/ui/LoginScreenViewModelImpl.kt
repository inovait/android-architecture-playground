package si.inova.androidarchitectureplayground.login.ui

import com.zhuinden.simplestack.StateChange
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import si.inova.androidarchitectureplayground.common.logging.ActionLogger
import si.inova.androidarchitectureplayground.login.LoginRepository
import si.inova.androidarchitectureplayground.navigation.keys.LoginScreenKey
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.di.BackstackScope
import si.inova.kotlinova.navigation.instructions.ReplaceTop
import si.inova.kotlinova.navigation.navigator.Navigator
import si.inova.kotlinova.navigation.services.ContributesScopedService
import si.inova.kotlinova.navigation.services.SingleScreenViewModel

@ContributesScopedService(LoginScreenViewModel::class)
@ContributesBinding(BackstackScope::class, binding<LoginScreenViewModel>())
class LoginScreenViewModelImpl @Inject constructor(
   private val resources: CoroutineResourceManager,
   private val navigator: Navigator,
   private val loginRepository: LoginRepository,
   private val actionLogger: ActionLogger,
) : LoginScreenViewModel, SingleScreenViewModel<LoginScreenKey>(resources.scope) {
   override val loginStatus = MutableStateFlow<Outcome<Unit>>(Outcome.Success(Unit))

   override fun login() {
      actionLogger.logAction { "login" }
      resources.launchResourceControlTask(loginStatus) {
         @Suppress("MagicNumber") // Delay is here just for demo purposes
         delay(500)
         loginRepository.setLoggedIn(true)
         emit(Outcome.Success(Unit))

         navigator.navigate(
            ReplaceTop(key.target, direction = StateChange.FORWARD)
         )
      }
   }
}
