package si.inova.androidarchitectureplayground.navigation.keys

import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.keys.base.BaseScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.base.ListKey

@Parcelize
@Stable
data object PostListScreenKey : BaseScreenKey(), ListKey
