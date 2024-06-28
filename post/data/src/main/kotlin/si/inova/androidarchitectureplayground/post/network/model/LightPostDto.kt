package si.inova.androidarchitectureplayground.post.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LightPostDto(
   val id: Int,
   val title: String,
)
