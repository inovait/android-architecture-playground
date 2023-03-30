package si.inova.androidarchitectureplayground.simplestack

import si.inova.androidarchitectureplayground.navigation.base.ScopedService
import si.inova.androidarchitectureplayground.navigation.base.Screen
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey

class ScreenRegistration<T : ScreenKey>(
   val screenClass: Class<out Screen<T>>,
   vararg val serviceClasses: Class<out ScopedService>
)
