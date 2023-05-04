package si.inova.androidarchitectureplayground.login

import kotlinx.coroutines.flow.Flow

interface LoginRepository {
   val isLoggedIn: Boolean

   fun isLoggedInFlow(): Flow<Boolean>
   suspend fun setLoggedIn(loggedIn: Boolean)
}
