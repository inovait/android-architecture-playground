package si.inova.androidarchitectureplayground.screens

import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.keys.SingleTopKey

@Parcelize
data class ScreenCKey(val number: Int) : SingleTopKey() {
   override val screenClass: String
      get() = "si.inova.androidarchitectureplayground.screens.ScreenC"
}
