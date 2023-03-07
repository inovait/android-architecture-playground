package si.inova.androidarchitectureplayground.screens

import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey

@Parcelize
object ProductListScreenKey : ScreenKey() {
   override val screenClass: String
      get() = "si.inova.androidarchitectureplayground.products.ui.ProductListScreen"

   override fun toString(): String {
      return "ProductListScreenKey"
   }
}
