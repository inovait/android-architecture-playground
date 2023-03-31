package si.inova.androidarchitectureplayground.navigation.keys

import kotlinx.parcelize.Parcelize
import si.inova.kotlinova.compose.result.ResultKey

@Parcelize
data class ScreenBKey(val result: ResultKey<String>? = null) : ScreenKey() {
   override fun getScopeTag(): String {
      return "CommonScope"
   }
}
