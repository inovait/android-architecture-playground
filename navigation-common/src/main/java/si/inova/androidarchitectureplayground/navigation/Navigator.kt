package si.inova.androidarchitectureplayground.navigation

import si.inova.androidarchitectureplayground.navigation.instructions.NavigationInstruction

interface Navigator {
   fun navigate(navigationInstruction: NavigationInstruction)
}
