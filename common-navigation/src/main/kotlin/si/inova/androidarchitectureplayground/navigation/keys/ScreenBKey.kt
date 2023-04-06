package si.inova.androidarchitectureplayground.navigation.keys

import kotlinx.parcelize.Parcelize
import si.inova.kotlinova.compose.result.ResultKey
import si.inova.kotlinova.navigation.screenkeys.ScreenKey

@Parcelize
data class ScreenBKey(val result: ResultKey<String>? = null) : ScreenKey() {
   override fun getScopeTag(): String {
      return "CommonScope"
   }
}
