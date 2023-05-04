package si.inova.androidarchitectureplayground.login

import kotlinx.coroutines.flow.Flow

interface LoginRepository {
   val isLoggedIn: Boolean

   fun isLoggedInFlow(): Flow<Boolean>
   fun setLoggedIn(loggedIn: Boolean)
}
