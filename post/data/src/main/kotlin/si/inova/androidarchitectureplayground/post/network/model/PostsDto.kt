package si.inova.androidarchitectureplayground.post.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostsDto(
   val posts: List<LightPostDto>,
   val total: Int,
)
