package si.inova.androidarchitectureplayground.user

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import si.inova.androidarchitectureplayground.common.pagination.PaginatedDataStream
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.kotlinova.core.outcome.Outcome

class FakeUserRepository : UserRepository {
   private val userDetailsMap = mutableMapOf<Int, MutableStateFlow<Outcome<User>>>()
   private val userList: MutableStateFlow<PaginatedDataStream.PaginationResult<List<User>>?> = MutableStateFlow(null)
   var numTimesNextPageCalled: Int = 0
   var numTimesForceLoadCalled: Int = 0

   fun setUserDetails(id: Int, user: Outcome<User>) {
      userDetailsMap.getOrPut(id) { MutableStateFlow(user) }.value = user
   }

   fun setUserList(users: PaginatedDataStream.PaginationResult<List<User>>) {
      userList.value = users
   }

   override fun getAllUsers(force: Boolean): PaginatedDataStream<List<User>> {
      if (force) {
         numTimesForceLoadCalled++
      }

      return object : PaginatedDataStream<List<User>> {
         override val data: Flow<PaginatedDataStream.PaginationResult<List<User>>>
            get() = userList.map {
               it ?: error("Fake user list not provided")
            }

         override fun nextPage() {
            numTimesNextPageCalled++
         }
      }
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
