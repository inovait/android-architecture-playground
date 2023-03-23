package si.inova.androidarchitectureplayground.navigation.keys

import kotlinx.parcelize.Parcelize

@Parcelize
object ScreenAKey : ScreenKey() {
   override val screenClass: String
      get() = "si.inova.androidarchitectureplayground.screens.ScreenA"

   override fun toString(): String {
      return "ScreenAKey"
   }
}
