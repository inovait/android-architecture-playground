package si.inova.androidarchitectureplayground.navigation.keys

import kotlinx.parcelize.Parcelize
import si.inova.kotlinova.navigation.screenkeys.ScreenKey

@Parcelize
data class UserDetailsScreenKey(val id: Int) : ScreenKey()
