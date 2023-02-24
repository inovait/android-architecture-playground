package si.inova.androidarchitectureplayground.navigation.instructions

import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.StateChange
import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.Navigator
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.SingleTopKey

@Parcelize
class OpenScreen(val screen: ScreenKey) : NavigationInstruction() {
   override fun performNavigation(backstack: Backstack) {
      if (screen is SingleTopKey && backstack.top<Any>().javaClass == screen.javaClass) {
         backstack.replaceTop(screen, StateChange.REPLACE)
      } else {
         backstack.goTo(screen)
      }
   }
}

fun Navigator.navigateTo(screen: ScreenKey) {
   navigate(OpenScreen(screen))
}
