package si.inova.androidarchitectureplayground.screens

import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.StateChange
import kotlinx.parcelize.Parcelize

@Parcelize
object ScreenCKey : ScreenKey() {
   override val screenClass: String
      get() = "si.inova.androidarchitectureplayground.screens.ScreenC"

   override fun performNavigation(backstack: Backstack) {
      backstack.replaceTop(this, StateChange.REPLACE)
   }

   override fun getScopeTag(): String {
      return "CommonScope"
   }
}
