package si.inova.androidarchitectureplayground.post.ui.list

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import si.inova.androidarchitectureplaygroud.post.PostsRepository
import si.inova.androidarchitectureplaygroud.post.model.Post
import si.inova.androidarchitectureplayground.common.pagination.PaginatedDataStream
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.core.outcome.mapData
import si.inova.kotlinova.navigation.services.CoroutineScopedService
import javax.inject.Inject

@Stable
class PostListViewModel @Inject constructor(
   private val resources: CoroutineResourceManager,
   private val postRepository: PostsRepository,
) : CoroutineScopedService(resources.scope) {
   private val _postList = MutableStateFlow<Outcome<PostListState>>(Outcome.Progress(PostListState()))
   val postList: StateFlow<Outcome<PostListState>>
      get() = _postList

   private var postPaginatedList: PaginatedDataStream<List<Post>>? = null

   override fun onServiceRegistered() {
      loadPostList()
   }

   private fun loadPostList(force: Boolean = false): Unit = resources.launchResourceControlTask(_postList) {
      val list = postRepository.getAllPosts(force)
      postPaginatedList = list

      emitAll(
         list.data.map { paginationResult ->
            paginationResult.items.mapData {
               PostListState(it, paginationResult.hasAnyDataLeft)
            }
         }
      )
   }

   fun nextPage() {
      postPaginatedList?.nextPage()
   }

   fun refresh() {
      loadPostList(force = true)
   }
}
