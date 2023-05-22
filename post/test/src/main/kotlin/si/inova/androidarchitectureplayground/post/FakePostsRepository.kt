package si.inova.androidarchitectureplayground.post

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import si.inova.androidarchitectureplaygroud.post.PostsRepository
import si.inova.androidarchitectureplaygroud.post.model.Post
import si.inova.androidarchitectureplayground.common.pagination.PaginatedDataStream
import si.inova.kotlinova.core.outcome.Outcome

class FakePostsRepository : PostsRepository {
   private val postDetailsMap = mutableMapOf<Int, MutableStateFlow<Outcome<Post>>>()
   private val postList: MutableStateFlow<PaginatedDataStream.PaginationResult<List<Post>>?> = MutableStateFlow(null)
   var numTimesNextPageCalled: Int = 0
   var numTimesForceLoadCalled: Int = 0

   fun setPostDetails(id: Int, post: Outcome<Post>) {
      postDetailsMap.getOrPut(id) { MutableStateFlow(post) }.value = post
   }

   fun setPostList(posts: PaginatedDataStream.PaginationResult<List<Post>>) {
      postList.value = posts
   }

   override fun getAllPosts(force: Boolean): PaginatedDataStream<List<Post>> {
      if (force) {
         numTimesForceLoadCalled++
      }

      return object : PaginatedDataStream<List<Post>> {
         override val data: Flow<PaginatedDataStream.PaginationResult<List<Post>>>
            get() = postList.map {
               it ?: error("Fake post list not provided")
            }

         override fun nextPage() {
            numTimesNextPageCalled++
         }
      }
   }

   override fun getPostDetails(
      id: Int,
      force: Boolean
   ): Flow<Outcome<Post>> {
      if (force) {
         numTimesForceLoadCalled++
      }

      return postDetailsMap.get(id) ?: error("Fake post with id $id not provided")
   }
}
