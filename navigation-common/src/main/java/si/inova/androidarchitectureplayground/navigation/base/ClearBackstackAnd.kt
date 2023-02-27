package si.inova.androidarchitectureplayground.navigation.base

import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.instructions.NavigationInstruction
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey

@Parcelize
data class ClearBackstackAnd(val then: NavigationInstruction) : NavigationInstruction() {
   override fun performNavigation(backstack: List<ScreenKey>, context: NavigationContext): NavigationResult {
      return then.performNavigation(emptyList(), context)
   }
}
