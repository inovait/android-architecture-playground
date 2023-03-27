package si.inova.androidarchitectureplayground.migration

import si.inova.androidarchitectureplayground.navigation.Navigator

/**
 * Temporary interface for allowing legacy screens not created by Dagger (such as Fragments) to access Navigator
 */
interface NavigatorActivity {
   val navigator: Navigator
}
