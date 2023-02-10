package si.inova.androidarchitectureplayground.simplestack

import androidx.annotation.CallSuper
import si.inova.androidarchitectureplayground.screens.ScopedService
import si.inova.androidarchitectureplayground.screens.ScreenKey

abstract class SingleScreenViewModel<K : ScreenKey> : ScopedService {
   lateinit var key: K

   @CallSuper
   open fun init(key: K) {
      this.key = key
   }
}
