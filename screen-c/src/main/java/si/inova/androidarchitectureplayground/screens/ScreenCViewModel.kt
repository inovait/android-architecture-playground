package si.inova.androidarchitectureplayground.screens

import android.util.Log
import si.inova.androidarchitectureplayground.navigation.base.SingleScreenViewModel
import javax.inject.Inject
import kotlin.random.Random

class ScreenCViewModel @Inject constructor() : SingleScreenViewModel<ScreenCKey>() {
   var number by saved(0)

   override fun onServiceRegistered() {
      Log.d("ViewModel", "got key $key")

      if (number == 0) {
         number = Random.nextInt()
      }
   }
}
