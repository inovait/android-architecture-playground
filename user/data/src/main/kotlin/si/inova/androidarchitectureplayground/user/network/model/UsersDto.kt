package si.inova.androidarchitectureplayground.user.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UsersDto(
   val users: List<LightUserDto>,
   val total: Int,
)
