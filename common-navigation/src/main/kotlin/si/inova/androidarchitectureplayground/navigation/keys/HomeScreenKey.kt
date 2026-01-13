package si.inova.androidarchitectureplayground.navigation.keys

import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.keys.base.BaseSingleTopScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.base.TabContainerKey

@Parcelize
@Stable
data object HomeScreenKey : BaseSingleTopScreenKey(), TabContainerKey
