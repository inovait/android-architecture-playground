package si.inova.androidarchitectureplayground.navigation.keys

import kotlinx.parcelize.Parcelize
import si.inova.kotlinova.navigation.instructions.NavigationInstruction
import si.inova.kotlinova.navigation.screenkeys.ScreenKey

@Parcelize
data class LoginScreenKey(val target: NavigationInstruction) : ScreenKey()
