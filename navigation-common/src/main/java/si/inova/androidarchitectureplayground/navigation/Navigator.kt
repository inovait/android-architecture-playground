package si.inova.androidarchitectureplayground.navigation

import si.inova.androidarchitectureplayground.navigation.keys.NavigationKey

interface Navigator {
   fun navigateTo(key: NavigationKey)

   fun goBack()
}
