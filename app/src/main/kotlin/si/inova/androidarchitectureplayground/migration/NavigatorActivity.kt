package si.inova.androidarchitectureplayground.migration

import si.inova.kotlinova.navigation.navigator.Navigator

/**
 * Temporary interface for allowing legacy screens not created by Dagger (such as Fragments) to access Navigator
 */
interface NavigatorActivity {
   val navigator: Navigator
}
