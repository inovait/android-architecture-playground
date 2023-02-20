package si.inova.androidarchitectureplayground.screens

import android.util.Log
import si.inova.androidarchitectureplayground.navigation.base.SingleScreenViewModel
import javax.inject.Inject

class ScreenAViewModel @Inject constructor() : SingleScreenViewModel<ScreenAKey>() {
   override fun init(key: ScreenAKey) {
      super.init(key)

      Log.d("ViewModel", "got key $key")
   }
}
