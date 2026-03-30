package si.inova.androidarchitectureplayground.post.network.model

import kotlinx.serialization.Serializable

@Serializable
data class PostsDto(
   val posts: List<LightPostDto>,
   val total: Int,
)
