package si.inova.androidarchitectureplayground.user

import androidx.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import si.inova.androidarchitectureplayground.common.pagination.PaginatedDataStream
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.kotlinova.core.outcome.Outcome

interface UserRepository {
   fun getAllUsers(force: Boolean = false): PagingSource<Int, User>
   fun getUserDetails(id: Int, force: Boolean = false): Flow<Outcome<User>>
}
