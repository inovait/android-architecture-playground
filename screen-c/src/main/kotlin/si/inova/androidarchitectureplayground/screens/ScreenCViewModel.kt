package si.inova.androidarchitectureplayground.screens

import androidx.compose.runtime.Stable
import si.inova.androidarchitectureplayground.navigation.base.ScopedService

@Stable
interface ScreenCViewModel : ScopedService {
   var number: Int
}
