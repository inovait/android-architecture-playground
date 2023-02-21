package si.inova.androidarchitectureplayground.simplestack

import com.zhuinden.simplestack.Backstack
import si.inova.androidarchitectureplayground.navigation.Navigator
import si.inova.androidarchitectureplayground.navigation.keys.NavigationKey
import javax.inject.Inject

class SimpleStackNavigator @Inject constructor(private val backstack: Backstack) : Navigator {
   override fun navigateTo(key: NavigationKey) {
      key.performNavigation(backstack)
   }

   override fun goBack() {
      backstack.goBack()
   }
}
