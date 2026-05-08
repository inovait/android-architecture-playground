package si.inova.androidarchitectureplayground

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import si.inova.kotlinova.navigation.screenkeys.ScreenKey

@AssistedInject
class MainViewModel : ViewModel() {
   private val _startingScreens = MutableStateFlow<List<ScreenKey>?>(null)
   val startingScreens: StateFlow<List<ScreenKey>?> = _startingScreens

   init {
      viewModelScope.launch {
         // _startingScreens.value = TODO
         error("Initial screen not provided")
      }
   }

   @AssistedFactory
   fun interface Factory {
      fun create(): MainViewModel
   }
}
