package si.inova.androidarchitectureplayground

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
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

class MainViewModel @AssistedInject constructor(
   private val loginRepository: LoginRepository,
   @Assisted
   private val startIntent: Intent,
) : ViewModel() {
   private val _startingScreen = MutableStateFlow<ScreenKey?>(null)
   val startingScreen: StateFlow<ScreenKey?> = _startingScreen

   init {
      viewModelScope.launch {
         if (startIntent.getBooleanExtra(BENCHMARK_AUTO_LOGIN_EXTRA, false)) {
            loginRepository.setLoggedIn(true)
         } else {
            @Suppress("MagicNumber") // Just a demo to show that splash screen is working
            delay(500)
         }

         // Load login via flow first to ensure non-suspending LoginRepository.isLoggedIn is initialized
         val isLogegdIn = loginRepository.isLoggedInFlow().first()

         _startingScreen.value = if (isLogegdIn) {
            HomeScreenKey()
         } else {
            LoginScreenKey(OpenScreen(HomeScreenKey()))
         }
      }
   }

   @AssistedFactory
   interface Factory {
      fun create(intent: Intent): MainViewModel
   }
}

private const val BENCHMARK_AUTO_LOGIN_EXTRA = "BENCHMARK_AUTO_LOGIN"
