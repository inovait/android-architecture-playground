package si.inova.androidarchitectureplaygroud.post.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LightPostDto(
   val id: Int,
   val title: String
)
