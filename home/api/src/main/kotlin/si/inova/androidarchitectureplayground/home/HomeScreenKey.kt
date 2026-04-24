package si.inova.androidarchitectureplayground.home

import kotlinx.serialization.Serializable
import si.inova.androidarchitectureplayground.navigation.keys.base.BaseScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.base.TabContainerKey

@Serializable
data object HomeScreenKey : BaseScreenKey(), TabContainerKey
