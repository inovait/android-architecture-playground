package si.inova.androidarchitectureplayground.navigation

import si.inova.androidarchitectureplayground.navigation.instructions.NavigationInstruction

fun interface Navigator {
   fun navigate(navigationInstruction: NavigationInstruction)
}
