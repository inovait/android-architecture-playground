package si.inova.androidarchitectureplayground.simplestack

import si.inova.androidarchitectureplayground.screens.ScreenKey

interface Navigator {
   fun navigateTo(key: ScreenKey)
}
