package si.inova.androidarchitectureplayground.screens

import kotlinx.parcelize.Parcelize

@Parcelize
data class ScreenCKey(val number: Int) : SingleTopKey() {
   override val screenClass: String
      get() = "si.inova.androidarchitectureplayground.screens.ScreenC"

   override fun getScopeTag(): String {
      return "CommonScope"
   }
}
