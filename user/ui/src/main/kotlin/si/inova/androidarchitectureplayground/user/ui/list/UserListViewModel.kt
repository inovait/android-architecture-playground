package si.inova.androidarchitectureplayground.user.ui.list

import kotlinx.coroutines.flow.StateFlow
import si.inova.androidarchitectureplayground.paging.PagedList
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.services.ScopedService

interface UserListViewModel : ScopedService {
   val userList: StateFlow<Outcome<PagedList<User>>>
   fun refresh()
}
