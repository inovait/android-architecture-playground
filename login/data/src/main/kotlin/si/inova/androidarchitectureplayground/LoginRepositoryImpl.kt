package si.inova.androidarchitectureplayground

import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import si.inova.androidarchitectureplayground.login.LoginRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ContributesBinding(ApplicationScope::class)
class LoginRepositoryImpl @Inject constructor() : LoginRepository {
   private val isLoggedinFlow = MutableStateFlow(false)

   override val isLoggedIn: Boolean
      get() = isLoggedinFlow.value

   override fun isLoggedInFlow(): Flow<Boolean> {
      return isLoggedinFlow
   }

   override fun setLoggedIn(loggedIn: Boolean) {
      isLoggedinFlow.value = loggedIn
   }
}
