package si.inova.androidarchitectureplayground.screens

import android.util.Log
import si.inova.androidarchitectureplayground.navigation.base.SingleScreenViewModel
import javax.inject.Inject

class ScreenCViewModel @Inject constructor() : SingleScreenViewModel<ScreenCKey>() {
   override fun init(key: ScreenCKey) {
      super.init(key)

      Log.d("ViewModel", "got key $key")
   }
}
