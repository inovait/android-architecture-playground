package si.inova.androidarchitectureplayground.screens

import kotlinx.parcelize.Parcelize

@Parcelize
object ScreenAKey : ScreenKey() {
   override val screenClass: String
      get() = "si.inova.androidarchitectureplayground.screens.ScreenA"
}
