package si.inova.androidarchitectureplayground.navigation.keys

import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.instructions.NavigationInstruction

@Parcelize
data class LoginScreenKey(val target: NavigationInstruction) : ScreenKey()
