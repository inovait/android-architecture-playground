package si.inova.androidarchitectureplayground.user.ui.details

import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import si.inova.androidarchitectureplayground.common.flow.AwayDetectorFlow
import si.inova.androidarchitectureplayground.common.logging.ActionLogger
import si.inova.androidarchitectureplayground.user.UserDetailsScreenKey
import si.inova.androidarchitectureplayground.user.UserRepository
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.di.BackstackScope
import si.inova.kotlinova.navigation.services.ScopedService
import si.inova.kotlinova.navigation.services.SingleScreenViewModel
import kotlin.time.Duration.Companion.seconds

@ContributesIntoMap(BackstackScope::class, binding = binding<ScopedService>())
@ClassKey(UserDetailsViewModel::class)
internal class UserDetailsViewModel(
   private val resources: CoroutineResourceManager,
   private val userRepository: UserRepository,
   private val actionLogger: ActionLogger,
) : SingleScreenViewModel<UserDetailsScreenKey>(resources.scope) {
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
