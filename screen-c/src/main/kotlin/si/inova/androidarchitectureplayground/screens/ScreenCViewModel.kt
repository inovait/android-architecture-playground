package si.inova.androidarchitectureplayground.screens

import androidx.compose.runtime.Stable
import si.inova.kotlinova.navigation.services.ScopedService

@Stable
interface ScreenCViewModel : ScopedService {
   var number: Int
}
