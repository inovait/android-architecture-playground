package si.inova.androidarchitectureplayground.screens

import androidx.compose.runtime.Composable

interface Screen<T : ScreenKey> {
   @Composable fun Content()
}
