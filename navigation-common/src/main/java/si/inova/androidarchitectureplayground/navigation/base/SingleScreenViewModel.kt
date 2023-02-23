package si.inova.androidarchitectureplayground.navigation.base

import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey

abstract class SingleScreenViewModel<K : ScreenKey> : SaveableScopedService() {
   lateinit var key: K
}
