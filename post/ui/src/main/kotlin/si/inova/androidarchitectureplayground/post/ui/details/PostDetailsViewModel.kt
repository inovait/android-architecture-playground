package si.inova.androidarchitectureplayground.post.ui.details

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import si.inova.androidarchitectureplaygroud.post.PostsRepository
import si.inova.androidarchitectureplaygroud.post.model.Post
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.services.CoroutineScopedService
import javax.inject.Inject

@Stable
class PostDetailsViewModel @Inject constructor(
   private val resources: CoroutineResourceManager,
   private val postRepository: PostsRepository,
) : CoroutineScopedService(resources.scope) {
   private val _postDetails = MutableStateFlow<Outcome<Post>>(Outcome.Progress())
   val postDetails: StateFlow<Outcome<Post>>
      get() = _postDetails

   private var postId: Int? = null

   fun startLoading(newPostId: Int) {
      if (postId != newPostId) {
         postId = newPostId
         loadPost()
      }
   }

   fun refresh() {
      loadPost(force = true)
   }

   private fun loadPost(force: Boolean = false) {
      val postId = postId ?: return
      resources.launchResourceControlTask(_postDetails) {
         emitAll(postRepository.getPostDetails(postId, force))
      }
   }
}
