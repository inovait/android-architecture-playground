package si.inova.androidarchitectureplayground.post.ui.list

import si.inova.androidarchitectureplayground.post.model.Post

data class PostListState(
   val posts: List<Post> = emptyList(),
   val hasAnyDataLeft: Boolean = false,
)
