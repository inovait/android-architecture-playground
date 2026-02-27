package si.inova.androidarchitectureplayground.navigation.keys

import kotlinx.serialization.Serializable
import si.inova.androidarchitectureplayground.navigation.keys.base.BaseScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.base.DetailKey

@Serializable
data class UserDetailsScreenKey(val id: Int) : BaseScreenKey(), DetailKey
