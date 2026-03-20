package si.inova.androidarchitectureplayground.user.ui.details

import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import si.inova.androidarchitectureplayground.common.flow.AwayDetectorFlow
import si.inova.androidarchitectureplayground.common.logging.ActionLogger
import si.inova.androidarchitectureplayground.navigation.services.BaseViewModel
import si.inova.androidarchitectureplayground.user.UserDetailsScreenKey
import si.inova.androidarchitectureplayground.user.UserRepository
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.services.ContributesScopedService
import kotlin.time.Duration.Companion.seconds

@ContributesScopedService
@Inject
class UserDetailsViewModel(
   resourcesFactory: CoroutineResourceManager.Factory,
   private val userRepository: UserRepository,
   private val actionLogger: ActionLogger,
) : BaseViewModel<UserDetailsScreenKey>(resourcesFactory) {
   private val _userDetails = MutableStateFlow<Outcome<User>>(Outcome.Progress())
   val userDetails: StateFlow<Outcome<User>>
      get() = _userDetails

   override fun onServiceRegistered() {
      actionLogger.logAction { "UserDetailsViewModel.onServiceRegistered" }
      loadUser()
   }

   fun refresh() {
      actionLogger.logAction { "UserDetailsViewModel.refresh()" }
      loadUser(force = true)
   }

   private fun loadUser(force: Boolean = false) {
      actionLogger.logAction { "UserDetailsViewModel.loadUser(force = $force)" }
      resources.launchResourceControlTask(_userDetails) {
         emitAll(
            AwayDetectorFlow(10.seconds).flatMapLatest {
               userRepository.getUserDetails(key.id, force)
            }
         )
      }
   }
}
