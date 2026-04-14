package si.inova.androidarchitectureplayground.post.ui.details

import dev.zacsweers.metro.ClassKey
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.binding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import si.inova.androidarchitectureplayground.common.flow.AwayDetectorFlow
import si.inova.androidarchitectureplayground.common.logging.ActionLogger
import si.inova.androidarchitectureplayground.post.PostDetailsScreenKey
import si.inova.androidarchitectureplayground.post.PostsRepository
import si.inova.androidarchitectureplayground.post.model.Post
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.di.BackstackScope
import si.inova.kotlinova.navigation.services.ScopedService
import si.inova.kotlinova.navigation.services.SingleScreenViewModel

@ContributesIntoMap(BackstackScope::class, binding = binding<ScopedService>())
@ClassKey(PostDetailsViewModel::class)
internal class PostDetailsViewModel(
   private val resources: CoroutineResourceManager,
   private val postRepository: PostsRepository,
   private val actionLogger: ActionLogger,
) : SingleScreenViewModel<PostDetailsScreenKey>(resources.scope) {
   private val _postDetails = MutableStateFlow<Outcome<Post>>(Outcome.Progress())
   val postDetails: StateFlow<Outcome<Post>>
      get() = _postDetails

   override fun onServiceRegistered() {
      actionLogger.logAction { "onServiceRegistered" }
      loadPost()
   }

   fun refresh() {
      actionLogger.logAction { "refresh" }
      loadPost(force = true)
   }

   private fun loadPost(force: Boolean = false) {
      resources.launchResourceControlTask(_postDetails) {
         emitAll(
            AwayDetectorFlow().flatMapLatest {
               postRepository.getPostDetails(
                  key.id,
                  force
               )
            }
         )
      }
   }
}
