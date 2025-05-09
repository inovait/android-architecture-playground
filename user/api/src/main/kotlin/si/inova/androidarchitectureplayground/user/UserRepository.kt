package si.inova.androidarchitectureplayground.user

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.kotlinova.core.outcome.Outcome

interface UserRepository {
   fun getAllUsers(): Flow<PagingData<User>>
   fun getUserDetails(id: Int, force: Boolean = false): Flow<Outcome<User>>
}
