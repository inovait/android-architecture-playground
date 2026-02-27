package si.inova.androidarchitectureplayground.navigation.keys

import kotlinx.serialization.Serializable
import si.inova.androidarchitectureplayground.navigation.keys.base.BaseScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.base.ListKey

@Serializable
data object PostListScreenKey : BaseScreenKey(), ListKey
