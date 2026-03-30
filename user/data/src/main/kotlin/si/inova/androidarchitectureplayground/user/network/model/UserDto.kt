package si.inova.androidarchitectureplayground.user.network.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
   val id: Int,
   val firstName: String,
   val lastName: String,
   val maidenName: String,
   val age: Int,
   val gender: String,
   val email: String,
   val phone: String,
   val hair: Hair,
) {
   @Serializable
   data class Hair(
      val color: String,
      val type: String,
   )
}
