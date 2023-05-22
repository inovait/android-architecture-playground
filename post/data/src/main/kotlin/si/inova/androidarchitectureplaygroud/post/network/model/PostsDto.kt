package si.inova.androidarchitectureplaygroud.post.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class PostsDto(
   val posts: List<LightPostDto>,
   val total: Int
)
