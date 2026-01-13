package si.inova.androidarchitectureplayground.navigation.keys

import androidx.compose.runtime.Stable
import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.keys.base.BaseScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.base.DetailKey

@Parcelize
@Stable
data class UserDetailsScreenKey(val id: Int) : BaseScreenKey(), DetailKey
