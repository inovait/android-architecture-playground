package si.inova.androidarchitectureplayground.user.ui.details

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import si.inova.androidarchitectureplayground.common.logging.ActionLogger
import si.inova.androidarchitectureplayground.user.UserRepository
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.services.CoroutineScopedService
import javax.inject.Inject

@Stable
class UserDetailsViewModel @Inject constructor(
   private val resources: CoroutineResourceManager,
   private val userRepository: UserRepository,
   private val actionLogger: ActionLogger,
) : CoroutineScopedService(resources.scope) {
   private val _userDetails = MutableStateFlow<Outcome<User>>(Outcome.Progress())
   val userDetails: StateFlow<Outcome<User>>
      get() = _userDetails

   private var userId: Int? = null

   fun startLoading(newUserId: Int) {
      actionLogger.logAction { "UserDetailsViewModel.startLoading(newUserId = $newUserId)" }
      if (userId != newUserId) {
         userId = newUserId
         loadUser()
      }
   }

   fun refresh() {
      actionLogger.logAction { "UserDetailsViewModel.refresh()" }
      loadUser(force = true)
   }

   private fun loadUser(force: Boolean = false) {
      actionLogger.logAction { "UserDetailsViewModel.loadUser(force = $force)" }
      val userId = userId ?: return
      resources.launchResourceControlTask(_userDetails) {
         emitAll(userRepository.getUserDetails(userId, force))
      }
   }
}
