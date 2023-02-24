package si.inova.androidarchitectureplayground.simplestack

import com.zhuinden.simplestack.Backstack
import si.inova.androidarchitectureplayground.navigation.Navigator
import si.inova.androidarchitectureplayground.navigation.instructions.NavigationInstruction
import javax.inject.Inject

class SimpleStackNavigator @Inject constructor(private val backstack: Backstack) : Navigator {
   override fun navigate(navigationInstruction: NavigationInstruction) {
      navigationInstruction.performNavigation(backstack)
   }

   override fun goBack() {
      backstack.goBack()
   }
}
