package si.inova.androidarchitectureplayground.navigation.keys

import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.keys.base.BaseScreenKey

@Parcelize
data class UserDetailsScreenKey(val id: Int) : BaseScreenKey()
