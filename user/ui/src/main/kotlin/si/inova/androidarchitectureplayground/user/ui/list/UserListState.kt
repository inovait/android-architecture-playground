package si.inova.androidarchitectureplayground.user.ui.list

import androidx.paging.PagingData
import si.inova.androidarchitectureplayground.user.model.User

data class UserListState(
   val users: PagedList<User>,
)
