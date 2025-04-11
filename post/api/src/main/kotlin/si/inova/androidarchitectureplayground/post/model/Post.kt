package si.inova.androidarchitectureplayground.post.model

import androidx.compose.runtime.Immutable

@Immutable
data class Post(
   val id: Int,
   val title: String,
   val body: String? = null,
   val userId: Int? = null,
   val tags: List<String>? = null,
   val numReactions: Int? = null,
   val image: String? = null,
)
