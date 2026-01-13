package si.inova.androidarchitectureplayground.navigation.keys

import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.conditions.NoLoginRedirectKey
import si.inova.androidarchitectureplayground.navigation.keys.base.BaseScreenKey
import si.inova.kotlinova.navigation.instructions.NavigationInstruction

@Parcelize
@Stable
data class LoginScreenKey(val target: NavigationInstruction) : BaseScreenKey(), NoLoginRedirectKey
