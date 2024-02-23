package si.inova.androidarchitectureplayground.user.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LightUserDto(
   val id: Int,
   val firstName: String,
   val lastName: String,
)
