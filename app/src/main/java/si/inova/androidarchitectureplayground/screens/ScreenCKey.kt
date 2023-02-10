package si.inova.androidarchitectureplayground.screens

import kotlinx.parcelize.Parcelize

@Parcelize
object ScreenCKey : ScreenKey() {
   override val screenClass: String
      get() = "si.inova.androidarchitectureplayground.screens.ScreenC"

   override fun getScopeTag(): String {
      return "CommonScope"
   }
}
