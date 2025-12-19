package si.inova.androidarchitectureplayground

import android.content.Intent
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import si.inova.androidarchitectureplayground.login.LoginRepository
import si.inova.androidarchitectureplayground.navigation.keys.HomeScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.LoginScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.PostListScreenKey
import si.inova.kotlinova.navigation.instructions.MultiNavigationInstructions
import si.inova.kotlinova.navigation.instructions.OpenScreen
import si.inova.kotlinova.navigation.screenkeys.ScreenKey

@AssistedInject
class MainViewModel(
   private val loginRepository: LoginRepository,
   private val generalPreferences: DataStore<Preferences>,
   @Assisted
   private val startIntent: Intent,
) : ViewModel() {
   private val _startingScreens = MutableStateFlow<List<ScreenKey>?>(null)
   val startingScreens: StateFlow<List<ScreenKey>?> = _startingScreens

   init {
      viewModelScope.launch {
         // Ensure preferences are loaded at the startup
         generalPreferences.data.first()

         if (startIntent.getBooleanExtra(BENCHMARK_AUTO_LOGIN_EXTRA, false)) {
            loginRepository.setLoggedIn(true)
         } else {
            @Suppress("MagicNumber") // Just a demo to show that splash screen is working
            delay(500)
         }

         // Load login via flow first to ensure non-suspending LoginRepository.isLoggedIn is initialized
         val isLogegdIn = loginRepository.isLoggedInFlow().first()

         _startingScreens.value = if (isLogegdIn) {
            listOf(HomeScreenKey, PostListScreenKey)
         } else {
            listOf(LoginScreenKey(MultiNavigationInstructions(OpenScreen(HomeScreenKey), OpenScreen(PostListScreenKey))))
         }
      }
   }

   @AssistedFactory
   fun interface Factory {
      fun create(startIntent: Intent): MainViewModel
   }
}

private const val BENCHMARK_AUTO_LOGIN_EXTRA = "BENCHMARK_AUTO_LOGIN"
