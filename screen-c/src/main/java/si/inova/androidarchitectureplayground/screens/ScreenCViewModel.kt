package si.inova.androidarchitectureplayground.screens

import si.inova.androidarchitectureplayground.navigation.base.ScopedService

interface ScreenCViewModel : ScopedService {
   var number: Int
}
