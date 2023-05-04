package si.inova.androidarchitectureplayground

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import si.inova.androidarchitectureplayground.navigation.keys.HomeScreenKey
import si.inova.kotlinova.navigation.screenkeys.ScreenKey

class MainViewModel : ViewModel() {
   private val _startingScreen = MutableStateFlow<ScreenKey?>(null)
   val startingScreen: StateFlow<ScreenKey?> = _startingScreen

   init {
      viewModelScope.launch {
         @Suppress("MagicNumber") // Just a demo
         delay(500)
         _startingScreen.value = HomeScreenKey()
      }
   }
}
