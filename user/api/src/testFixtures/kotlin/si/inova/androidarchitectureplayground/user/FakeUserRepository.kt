package si.inova.androidarchitectureplayground.user

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.kotlinova.core.outcome.Outcome

class FakeUserRepository : UserRepository {
   private val userDetailsMap = mutableMapOf<Int, MutableStateFlow<Outcome<User>>>()
   private val userList: MutableStateFlow<PagingData<User>?> = MutableStateFlow(null)
   var numTimesForceLoadCalled: Int = 0

   fun setUserDetails(id: Int, user: Outcome<User>) {
      userDetailsMap.getOrPut(id) { MutableStateFlow(user) }.value = user
   }

   fun setUserList(users: PagingData<User>) {
      userList.value = users
   }

   override fun getAllUsers(): Flow<PagingData<User>> {
      return userList.map { it ?: error("userList not faked") }
   }

   override fun getUserDetails(
      id: Int,
      force: Boolean,
   ): Flow<Outcome<User>> {
      if (force) {
         numTimesForceLoadCalled++
      }

      return userDetailsMap.get(id) ?: error("Fake user with id $id not provided")
   }
}
