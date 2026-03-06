package si.inova.androidarchitectureplayground.post

import kotlinx.serialization.Serializable
import si.inova.androidarchitectureplayground.navigation.keys.base.BaseScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.base.DetailKey

@Serializable
data class PostDetailsScreenKey(val id: Int) : BaseScreenKey(), DetailKey
