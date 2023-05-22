package si.inova.androidarchitectureplaygroud.post.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostDto(
   val id: Int,
   val title: String,
   val body: String,
   val userId: Int,
   val tags: List<String>,
   val reactions: Int,
   val image: String = "https://i.dummyjson.com/data/products/$id/1.jpg"
)
