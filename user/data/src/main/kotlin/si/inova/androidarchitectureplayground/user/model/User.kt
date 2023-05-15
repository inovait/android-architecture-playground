package si.inova.androidarchitectureplayground.user.model

data class User(
   val id: Int,
   val firstName: String,
   val lastName: String,
   val maidenName: String? = null,
   val age: Int? = null,
   val gender: String? = null,
   val email: String? = null,
   val phone: String? = null,
   val hair: Hair? = null
) {
   data class Hair(
      val color: String,
      val type: String
   )
}
