package si.inova.androidarchitectureplayground.navigation.keys

import kotlinx.parcelize.Parcelize
import si.inova.androidarchitectureplayground.navigation.keys.base.BaseScreenKey

@Parcelize
data class HomeUsersScreenKey(val userDetailsId: String? = null) : BaseScreenKey()
