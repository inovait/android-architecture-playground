package si.inova.androidarchitectureplayground.navigation.keys

import kotlinx.parcelize.Parcelize

@Parcelize
object ScreenBKey : ScreenKey() {
   override val screenClass: String
      get() = "si.inova.androidarchitectureplayground.screens.ScreenB"

   override fun getScopeTag(): String {
      return "CommonScope"
   }

   override fun toString(): String {
      return "ScreenBKey"
   }
}
