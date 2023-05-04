package si.inova.androidarchitectureplayground.login

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeLoginRepository : LoginRepository {
   private val loggedinFlow = MutableStateFlow(false)

   override val isLoggedIn: Boolean
      get() = loggedinFlow.value

   override fun isLoggedInFlow(): Flow<Boolean> {
      return loggedinFlow
   }

   override fun setLoggedIn(loggedIn: Boolean) {
      loggedinFlow.value = loggedIn
   }
}
