package si.inova.androidarchitectureplayground

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deliveryhero.whetstone.viewmodel.ContributesViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import si.inova.androidarchitectureplayground.login.LoginRepository
import si.inova.androidarchitectureplayground.navigation.keys.HomeScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.LoginScreenKey
import si.inova.kotlinova.navigation.instructions.OpenScreen
import si.inova.kotlinova.navigation.screenkeys.ScreenKey
import javax.inject.Inject

@ContributesViewModel
class MainViewModel @Inject constructor(
   private val loginRepository: LoginRepository
) : ViewModel() {
   private val _startingScreen = MutableStateFlow<ScreenKey?>(null)
   val startingScreen: StateFlow<ScreenKey?> = _startingScreen

   init {
      viewModelScope.launch {
         @Suppress("MagicNumber") // Just a demo
         delay(500)

         // Load login via flow first to ensure non-suspending LoginRepository.isLoggedIn is initialized
         val isLogegdIn = loginRepository.isLoggedInFlow().first()

         _startingScreen.value = if (isLogegdIn) {
            HomeScreenKey()
         } else {
            LoginScreenKey(OpenScreen(HomeScreenKey()))
         }
      }
   }
}
