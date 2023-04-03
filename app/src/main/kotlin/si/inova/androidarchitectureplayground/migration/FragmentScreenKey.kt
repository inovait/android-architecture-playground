package si.inova.androidarchitectureplayground.migration

import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey

/**
 * Key for the [FragmentScreen].
 */
abstract class FragmentScreenKey : ScreenKey() {
   /**
    * tag for the fragment. It has to be defined in the child class due to how Parcelize works.
    * You can default to `UUID.randomUUID().toString()`.
    */
   abstract val tag: String
}
