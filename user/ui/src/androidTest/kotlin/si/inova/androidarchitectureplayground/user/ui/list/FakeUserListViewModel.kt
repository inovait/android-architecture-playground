package si.inova.androidarchitectureplayground.user.ui.list

import kotlinx.coroutines.flow.MutableStateFlow
import si.inova.androidarchitectureplayground.paging.PagedList
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.kotlinova.core.outcome.Outcome

class FakeUserListViewModel : UserListViewModel {
   var refreshCalled: Boolean = false

   override val userList = MutableStateFlow<Outcome<PagedList<User>>>(Outcome.Progress())

   override fun refresh() {
      refreshCalled = true
   }
}
