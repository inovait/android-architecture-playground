package si.inova.androidarchitectureplayground.user

import kotlinx.serialization.Serializable
import si.inova.androidarchitectureplayground.navigation.keys.base.BaseScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.base.ListKey

@Serializable
data object UserListScreenKey : BaseScreenKey(), ListKey
