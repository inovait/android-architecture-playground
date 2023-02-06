package si.inova.androidarchitectureplayground.screens

import androidx.compose.runtime.Composable

abstract class Screen<T : ScreenKey> {
   lateinit var key: T
   abstract @Composable fun Content()
}
