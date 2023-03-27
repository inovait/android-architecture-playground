package si.inova.androidarchitectureplayground.navigation.keys

import kotlinx.parcelize.Parcelize

@Parcelize
object ScreenAKey : NoArgsScreenKey() {
   override val screenClass: String
      get() = "si.inova.androidarchitectureplayground.screens.ScreenA"
}
