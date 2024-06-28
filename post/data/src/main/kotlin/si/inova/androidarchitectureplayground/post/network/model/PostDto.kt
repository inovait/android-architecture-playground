package si.inova.androidarchitectureplayground.post.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostDto(
   val id: Int,
   val title: String,
   val body: String,
   val userId: Int,
   val tags: List<String>,
   val reactions: Reactions,
   val image: String = "https://i.dummyjson.com/data/products/$id/1.jpg",
) {
   @JsonClass(generateAdapter = true)
   data class Reactions(val likes: Int, val dislikes: Int)
}
