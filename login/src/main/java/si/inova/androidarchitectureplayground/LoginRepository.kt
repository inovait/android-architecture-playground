package si.inova.androidarchitectureplayground

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor() {
   private val isLoggedinFlow = MutableStateFlow(false)

   val isLoggedIn: Boolean
      get() = isLoggedinFlow.value

   fun isLoggedInFlow(): Flow<Boolean> {
      return isLoggedinFlow
   }

   fun setLoggedIn(loggedIn: Boolean) {
      isLoggedinFlow.value = loggedIn
   }
}
