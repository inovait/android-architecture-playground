package si.inova.androidarchitectureplayground.screens

import kotlinx.parcelize.Parcelize

@Parcelize
object ScreenBKey : ScreenKey() {
   override val screenClass: String
      get() = "si.inova.androidarchitectureplayground.screens.ScreenB"
}
