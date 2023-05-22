package si.inova.androidarchitectureplayground.navigation.keys

import kotlinx.parcelize.Parcelize
import si.inova.kotlinova.navigation.screenkeys.ScreenKey

@Parcelize
data class PostDetailsScreenKey(val id: Int) : ScreenKey()
