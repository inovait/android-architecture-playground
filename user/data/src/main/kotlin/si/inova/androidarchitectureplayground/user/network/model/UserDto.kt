package si.inova.androidarchitectureplayground.user.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UserDto(
   val id: Int,
   val firstName: String,
   val lastName: String,
   val maidenName: String,
   val age: Int,
   val gender: String,
   val email: String,
   val phone: String,
   val hair: Hair
) {
   @JsonClass(generateAdapter = true)
   data class Hair(
      val color: String,
      val type: String
   )
}
