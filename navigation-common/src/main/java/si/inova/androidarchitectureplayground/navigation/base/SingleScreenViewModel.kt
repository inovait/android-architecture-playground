package si.inova.androidarchitectureplayground.navigation.base

import kotlinx.coroutines.CoroutineScope
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey

/**
 * A ViewModel that is meant to be bound to a single screen. [key] will automatically get populated with that screen's key.
 *
 * Use [onServiceRegistered] method as your init method - key is guaranteed to be initialized here.
 *
 * @param [coroutineScope] See documentation for [CoroutineScopedService]
 */
abstract class SingleScreenViewModel<K : ScreenKey>(coroutineScope: CoroutineScope) :
   SaveableScopedService(coroutineScope) {
   lateinit var key: K
}
