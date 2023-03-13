package si.inova.androidarchitectureplayground.screens

import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey

@Parcelize
object MasterDetailDemoScreenKey : ScreenKey() {
   override val screenClass: String
      get() = "si.inova.androidarchitectureplayground.masterdetail.MasterDetailDemoScreen"

   override fun toString(): String {
      return "MasterDetailDemoScreenKey"
   }
}
