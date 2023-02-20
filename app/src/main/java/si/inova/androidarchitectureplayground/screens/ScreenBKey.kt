package si.inova.androidarchitectureplayground.screens

import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey

@Parcelize
object ScreenBKey : ScreenKey() {
   override val screenClass: String
      get() = "si.inova.androidarchitectureplayground.screens.ScreenB"

   override fun getScopeTag(): String {
      return "CommonScope"
   }
}
