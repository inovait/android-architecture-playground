package si.inova.androidarchitectureplayground.screens

import android.util.Log
import androidx.compose.runtime.Stable
import si.inova.androidarchitectureplayground.navigation.base.SingleScreenViewModel
import si.inova.androidarchitectureplayground.navigation.keys.ScreenAKey
import javax.inject.Inject

@Stable
class ScreenAViewModel @Inject constructor() : SingleScreenViewModel<ScreenAKey>() {
   override fun onServiceRegistered() {
      Log.d("ViewModel", "got key $key")
   }
}
