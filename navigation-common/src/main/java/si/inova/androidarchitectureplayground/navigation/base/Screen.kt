package si.inova.androidarchitectureplayground.navigation.base

import androidx.compose.runtime.Composable
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey

abstract class Screen<T : ScreenKey> {
   @Composable
   abstract fun Content(key: T)
}
