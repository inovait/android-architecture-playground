package si.inova.androidarchitectureplayground.user.ui.list

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject
import si.inova.androidarchitectureplayground.common.logging.ActionLogger
import si.inova.androidarchitectureplayground.paging.PagedList
import si.inova.androidarchitectureplayground.paging.PagingResult
import si.inova.androidarchitectureplayground.paging.toPagingResult
import si.inova.androidarchitectureplayground.user.UserRepository
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.services.CoroutineScopedService
import si.inova.kotlinova.navigation.services.InjectScopedService
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@InjectScopedService
@ContributesBinding(AppScope::class, boundType = UserListViewModel::class)
class UserListViewModelImpl @Inject constructor(
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
