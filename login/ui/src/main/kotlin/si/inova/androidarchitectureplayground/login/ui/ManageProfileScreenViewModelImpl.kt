package si.inova.androidarchitectureplayground.login.ui

import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import si.inova.androidarchitectureplayground.common.logging.ActionLogger
import si.inova.androidarchitectureplayground.login.LoginRepository
import si.inova.androidarchitectureplayground.navigation.keys.HomeScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.LoginScreenKey
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.di.BackstackScope
import si.inova.kotlinova.navigation.instructions.OpenScreen
import si.inova.kotlinova.navigation.instructions.ReplaceBackstack
import si.inova.kotlinova.navigation.navigator.Navigator
import si.inova.kotlinova.navigation.services.CoroutineScopedService
import javax.inject.Inject

@ContributesBinding(BackstackScope::class, ManageProfileScreenViewModel::class)
class ManageProfileScreenViewModelImpl @Inject constructor(
   private val resources: CoroutineResourceManager,
   private val navigator: Navigator,
   private val loginRepository: LoginRepository,
   private val actionLogger: ActionLogger,
) : CoroutineScopedService(resources.scope), ManageProfileScreenViewModel {
   override val logoutStatus = MutableStateFlow<Outcome<Unit>>(Outcome.Success(Unit))
   override fun logout() {
      actionLogger.logAction { "logout" }
      resources.launchResourceControlTask(logoutStatus) {
         @Suppress("MagicNumber") // Delay is here just for demo purposes
         delay(500)
         loginRepository.setLoggedIn(false)
         emit(Outcome.Success(Unit))

         navigator.navigate(
            ReplaceBackstack(LoginScreenKey(OpenScreen(HomeScreenKey())))
         )
      }
   }
}
