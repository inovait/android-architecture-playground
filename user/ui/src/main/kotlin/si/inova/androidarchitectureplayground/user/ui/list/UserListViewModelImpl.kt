package si.inova.androidarchitectureplayground.user.ui.list

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import si.inova.androidarchitectureplayground.common.logging.ActionLogger
import si.inova.androidarchitectureplayground.paging.PagedList
import si.inova.androidarchitectureplayground.paging.PagingResult
import si.inova.androidarchitectureplayground.paging.toPagingResult
import si.inova.androidarchitectureplayground.user.UserRepository
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.services.ContributesScopedService
import si.inova.kotlinova.navigation.services.CoroutineScopedService

@ContributesScopedService(UserListViewModel::class)
@ContributesBinding(AppScope::class, binding = binding<UserListViewModel>())
@Inject
class UserListViewModelImpl(
   private val resources: CoroutineResourceManager,
   private val userRepository: UserRepository,
   private val actionLogger: ActionLogger,
) : CoroutineScopedService(resources.scope), UserListViewModel {
   private val _userList = MutableStateFlow<Outcome<PagedList<User>>>(Outcome.Progress())
   override val userList: StateFlow<Outcome<PagedList<User>>>
      get() = _userList

   private var pagingResult: PagingResult<User>? = null

   override fun onServiceRegistered() {
      actionLogger.logAction { "UserListViewModel.onServiceRegistered()" }
      loadUserList()
   }

   private fun loadUserList() = resources.launchResourceControlTask(_userList) {
      val pagingResult = userRepository.getAllUsers()
         .toPagingResult(this)

      this@UserListViewModelImpl.pagingResult = pagingResult

      emitAll(pagingResult.data)
   }

   override fun refresh() {
      actionLogger.logAction { "UserListViewModel.refresh()" }
      pagingResult?.refresh()
   }
}
