package si.inova.androidarchitectureplayground.navigation.keys

import kotlinx.parcelize.Parcelize

@Parcelize
object ScreenBKey : NoArgsScreenKey() {
   override val screenClass: String
      get() = "si.inova.androidarchitectureplayground.screens.ScreenB"

   override fun getScopeTag(): String {
      return "CommonScope"
   }
}
