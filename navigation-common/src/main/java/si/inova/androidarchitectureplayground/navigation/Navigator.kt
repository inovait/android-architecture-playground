package si.inova.androidarchitectureplayground.navigation

import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey

interface Navigator {
   fun navigateTo(key: ScreenKey)

   fun goBack()
}
