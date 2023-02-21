package si.inova.androidarchitectureplayground.navigation.keys

import com.zhuinden.simplestack.Backstack

abstract class NavigationKey {
   abstract fun performNavigation(backstack: Backstack)
}
