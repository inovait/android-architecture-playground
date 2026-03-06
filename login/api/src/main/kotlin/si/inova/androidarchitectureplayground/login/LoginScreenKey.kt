package si.inova.androidarchitectureplayground.login

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import si.inova.androidarchitectureplayground.navigation.conditions.NoLoginRedirectKey
import si.inova.androidarchitectureplayground.navigation.keys.base.BaseScreenKey
import si.inova.kotlinova.navigation.instructions.NavigationInstruction

@Serializable
data class LoginScreenKey(val target: @Contextual NavigationInstruction) : BaseScreenKey(), NoLoginRedirectKey
