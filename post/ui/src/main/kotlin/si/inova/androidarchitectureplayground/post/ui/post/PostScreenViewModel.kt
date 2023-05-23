package si.inova.androidarchitectureplayground.post.ui.post

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import si.inova.androidarchitectureplaygroud.post.PostsRepository
import si.inova.androidarchitectureplaygroud.post.model.Post
import si.inova.androidarchitectureplayground.navigation.keys.PostScreenKey
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.services.SingleScreenViewModel
import javax.inject.Inject

class PostScreenViewModel @Inject constructor(
   private val resources: CoroutineResourceManager,
   private val postsRepository: PostsRepository
) : SingleScreenViewModel<PostScreenKey>(resources.scope) {
   private val _post = MutableStateFlow<Outcome<Post>>(Outcome.Progress())
   val post: StateFlow<Outcome<Post>> = _post

   override fun onServiceRegistered() {
      resources.launchResourceControlTask(_post) {
         val data = postsRepository.getPostDetails(key.id)
         value = Outcome.Success(data)
      }
   }
}
