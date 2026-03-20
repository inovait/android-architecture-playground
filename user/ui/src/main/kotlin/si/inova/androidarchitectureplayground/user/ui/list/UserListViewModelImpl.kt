package si.inova.androidarchitectureplayground.user.ui.list

import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import si.inova.androidarchitectureplayground.common.logging.ActionLogger
import si.inova.androidarchitectureplayground.navigation.services.BaseViewModel
import si.inova.androidarchitectureplayground.paging.PagedList
import si.inova.androidarchitectureplayground.paging.PagingResult
import si.inova.androidarchitectureplayground.paging.toPagingResult
import si.inova.androidarchitectureplayground.user.UserListScreenKey
import si.inova.androidarchitectureplayground.user.UserRepository
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.di.BackstackScope
import si.inova.kotlinova.navigation.services.ScopedService

@ContributesIntoMap(BackstackScope::class, binding = binding<ScopedService>())
@ClassKey(UserListViewModel::class)
internal class UserListViewModelImpl(
   resourcesFactory: CoroutineResourceManager.Factory,
   private val userRepository: UserRepository,
   private val actionLogger: ActionLogger,
) : BaseViewModel<UserListScreenKey>(resourcesFactory), UserListViewModel {
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
