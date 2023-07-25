package si.inova.androidarchitectureplayground.user.ui.list

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import si.inova.androidarchitectureplayground.user.UserRepository
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.core.outcome.mapData
import si.inova.kotlinova.navigation.services.CoroutineScopedService
import javax.inject.Inject

@Stable
class UserListViewModel @Inject constructor(
   private val resources: CoroutineResourceManager,
   private val userRepository: UserRepository
) : CoroutineScopedService(resources.scope) {
   private val viewModelScope = coroutineScope

   private val _userList = MutableStateFlow<Outcome<UserListState>>(Outcome.Progress(UserListState()))
   val userList: StateFlow<Outcome<UserListState>>
      get() = _userList

//   val  userList = userRepository.getAllUsers().map { users ->
//      users.mapData {
//         si.inova.androidarchitectureplayground.user.ui.list.UserListState(it)
//      }
//   }

   override fun onServiceRegistered() {
      loadUserList()
   }

   private fun loadUserList(force: Boolean = false) {
      viewModelScope.launch {
         val list = userRepository.getAllUsers(force)

         _userList.emitAll(
            list.map { users ->
               users.mapData {
                  UserListState(it)
               }
            }
         )
      }
   }

   fun nextPage() = Unit

   fun refresh() {
      loadUserList(force = true)
   }
}
