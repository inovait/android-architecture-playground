package si.inova.androidarchitectureplayground.navigation.keys

import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.keys.base.BaseSingleTopScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.base.TabScreenKey

@Parcelize
data object HomeScreenKey : BaseSingleTopScreenKey(), TabScreenKey
