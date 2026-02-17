package si.inova.androidarchitectureplayground.navigation.keys

import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.keys.base.BaseScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.base.TabContainerKey

@Parcelize
data object HomeScreenKey : BaseScreenKey(), TabContainerKey
