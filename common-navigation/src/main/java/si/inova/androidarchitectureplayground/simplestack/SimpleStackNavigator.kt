package si.inova.androidarchitectureplayground.simplestack

import com.zhuinden.simplestack.Backstack
import dagger.Lazy
import si.inova.androidarchitectureplayground.navigation.Navigator
import si.inova.androidarchitectureplayground.navigation.instructions.NavigationInstruction
import javax.inject.Inject

class SimpleStackNavigator @Inject constructor(
   private val backstack: Backstack,
   private val navigationContext: Lazy<NavigationContextImpl>
) : Navigator {
   override fun navigate(navigationInstruction: NavigationInstruction) {
      val res = navigationInstruction.performNavigation(backstack.getHistory(), navigationContext.get())

      backstack.setHistory(res.newBackstack, res.direction)
   }
}
