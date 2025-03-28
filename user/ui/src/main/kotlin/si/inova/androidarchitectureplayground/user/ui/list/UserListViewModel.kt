package si.inova.androidarchitectureplayground.user.ui.list

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import si.inova.androidarchitectureplayground.common.flow.AwayDetectorFlow
import si.inova.androidarchitectureplayground.common.logging.ActionLogger
import si.inova.androidarchitectureplayground.common.pagination.PaginatedDataStream
import si.inova.androidarchitectureplayground.user.UserRepository
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.core.outcome.mapData
import si.inova.kotlinova.navigation.services.CoroutineScopedService
import si.inova.kotlinova.navigation.services.InjectScopedService

@InjectScopedService
class UserListViewModel @Inject constructor(
   private val resources: CoroutineResourceManager,
   private val userRepository: UserRepository,
   private val actionLogger: ActionLogger,
) : CoroutineScopedService(resources.scope) {
   private val _userList = MutableStateFlow<Outcome<UserListState>>(Outcome.Progress(UserListState()))
   val userList: StateFlow<Outcome<UserListState>>
      get() = _userList

   private var userPaginatedList: PaginatedDataStream<List<User>>? = null

   override fun onServiceRegistered() {
      actionLogger.logAction { "UserListViewModel.onServiceRegistered()" }
      loadUserList()
   }

   private fun loadUserList(force: Boolean = false) = resources.launchResourceControlTask(_userList) {
      val usersFlow = AwayDetectorFlow().flatMapLatest {
         val list = userRepository.getAllUsers(force)
         userPaginatedList = list

         list.data
      }

      emitAll(
         usersFlow.map { paginationResult ->
            paginationResult.items.mapData {
               UserListState(it, paginationResult.hasAnyDataLeft)
            }
         }
      )
   }

   fun nextPage() {
      actionLogger.logAction { "UserListViewModel.nextPage()" }
      userPaginatedList?.nextPage()
   }

   fun refresh() {
      actionLogger.logAction { "UserListViewModel.refresh()" }
      loadUserList(force = true)
   }
}
