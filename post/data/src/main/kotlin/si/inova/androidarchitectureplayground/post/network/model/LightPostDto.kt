package si.inova.androidarchitectureplayground.post.network.model

import kotlinx.serialization.Serializable

@Serializable
data class LightPostDto(
   val id: Int,
   val title: String,
)
