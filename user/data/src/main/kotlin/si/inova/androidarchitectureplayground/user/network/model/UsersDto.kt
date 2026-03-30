package si.inova.androidarchitectureplayground.user.network.model

import kotlinx.serialization.Serializable

@Serializable
data class UsersDto(
   val users: List<LightUserDto>,
   val total: Int,
)
