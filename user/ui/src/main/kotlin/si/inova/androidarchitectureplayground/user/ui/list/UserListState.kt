package si.inova.androidarchitectureplayground.user.ui.list

import si.inova.androidarchitectureplayground.user.model.User

data class UserListState(
   val users: List<User> = emptyList(),
   val hasAnyDataLeft: Boolean = false
)
