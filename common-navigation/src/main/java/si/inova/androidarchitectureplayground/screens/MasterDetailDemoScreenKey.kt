package si.inova.androidarchitectureplayground.screens

import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.keys.NoArgsScreenKey

@Parcelize
object MasterDetailDemoScreenKey : NoArgsScreenKey() {
   override val screenClass: String
      get() = "si.inova.androidarchitectureplayground.masterdetail.MasterDetailDemoScreen"
}
