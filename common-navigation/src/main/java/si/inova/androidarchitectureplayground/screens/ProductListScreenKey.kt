package si.inova.androidarchitectureplayground.screens

import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.keys.NoArgsScreenKey

@Parcelize
object ProductListScreenKey : NoArgsScreenKey() {
   override val screenClass: String
      get() = "si.inova.androidarchitectureplayground.products.ui.ProductListScreen"
}
