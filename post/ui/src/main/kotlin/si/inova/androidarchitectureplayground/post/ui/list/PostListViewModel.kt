package si.inova.androidarchitectureplayground.post.ui.list

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import si.inova.androidarchitectureplayground.common.logging.ActionLogger
import si.inova.androidarchitectureplayground.common.pagination.PaginatedDataStream
import si.inova.androidarchitectureplayground.post.PostsRepository
import si.inova.androidarchitectureplayground.post.model.Post
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.core.outcome.mapData
import si.inova.kotlinova.navigation.services.CoroutineScopedService
import si.inova.kotlinova.navigation.services.InjectScopedService

@InjectScopedService
class PostListViewModel @Inject constructor(
   private val resources: CoroutineResourceManager,
   private val postRepository: PostsRepository,
   private val actionLogger: ActionLogger,
) : CoroutineScopedService(resources.scope) {
   private val _postList = MutableStateFlow<Outcome<PostListState>>(Outcome.Progress(PostListState()))
   val postList: StateFlow<Outcome<PostListState>>
      get() = _postList

   private var postPaginatedList: PaginatedDataStream<List<Post>>? = null

   override fun onServiceRegistered() {
      actionLogger.logAction { "PostListViewModel.onServiceRegistered()" }
      loadPostList()
   }

   private fun loadPostList(force: Boolean = false) = resources.launchResourceControlTask(_postList) {
      actionLogger.logAction { "PostListViewModel.loadPostList(force = $force)" }

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
      actionLogger.logAction { "PostListViewModel.nextPage()" }
      postPaginatedList?.nextPage()
   }

   fun refresh() {
      actionLogger.logAction { "PostListViewModel.refresh()" }
      loadPostList(force = true)
   }
}
