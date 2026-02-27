package si.inova.androidarchitectureplayground.navigation.keys

import kotlinx.serialization.Serializable
import si.inova.androidarchitectureplayground.navigation.keys.base.BaseSingleTopScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.base.TabContainerKey

@Serializable
data object HomeScreenKey : BaseSingleTopScreenKey(), TabContainerKey
