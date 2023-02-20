package si.inova.androidarchitectureplayground.navigation.base

import androidx.annotation.CallSuper
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey

abstract class SingleScreenViewModel<K : ScreenKey> : ScopedService {
   lateinit var key: K

   @CallSuper
   open fun init(key: K) {
      this.key = key
   }
}
