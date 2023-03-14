package si.inova.androidarchitectureplayground.navigation.keys

import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.base.ResultKey

@Parcelize
data class ScreenBKey(val result: ResultKey<String>? = null) : ScreenKey() {
   override val screenClass: String
      get() = "si.inova.androidarchitectureplayground.screens.ScreenB"

   override fun getScopeTag(): String {
      return "CommonScope"
   }
}
