package si.inova.androidarchitectureplayground.screens

import androidx.compose.runtime.Composable

abstract class Screen<T : ScreenKey> {
   @Composable
   abstract fun Content(key: T)
}
