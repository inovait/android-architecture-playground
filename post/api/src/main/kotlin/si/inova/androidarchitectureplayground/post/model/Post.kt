package si.inova.androidarchitectureplayground.post.model

data class Post(
   val id: Int,
   val title: String,
   val body: String? = null,
   val userId: Int? = null,
   val tags: List<String>? = null,
   val numReactions: Int? = null,
   val image: String? = null,
)
