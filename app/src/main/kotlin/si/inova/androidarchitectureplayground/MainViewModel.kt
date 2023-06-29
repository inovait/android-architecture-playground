package si.inova.androidarchitectureplayground

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import si.inova.kotlinova.navigation.screenkeys.ScreenKey
import javax.inject.Inject

class MainViewModel @Inject constructor() : ViewModel() {
   private val _startingScreen = MutableStateFlow<ScreenKey?>(null)
   val startingScreen: StateFlow<ScreenKey?> = _startingScreen

   init {
      viewModelScope.launch {
         // _startingScreen.value = TODO
         error("Initial screen not provided")
      }
   }
}
