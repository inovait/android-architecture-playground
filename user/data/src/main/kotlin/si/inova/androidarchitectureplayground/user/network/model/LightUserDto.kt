package si.inova.androidarchitectureplayground.user.network.model

import kotlinx.serialization.Serializable

@Serializable
data class LightUserDto(
   val id: Int,
   val firstName: String,
   val lastName: String,
)
