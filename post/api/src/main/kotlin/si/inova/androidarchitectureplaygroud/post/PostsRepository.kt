package si.inova.androidarchitectureplaygroud.post

import kotlinx.coroutines.flow.Flow
import si.inova.androidarchitectureplaygroud.post.model.Post
import si.inova.androidarchitectureplayground.common.pagination.PaginatedDataStream
import si.inova.kotlinova.core.outcome.Outcome

interface PostsRepository {
   fun getAllPosts(force: Boolean = false): PaginatedDataStream<List<Post>>
   fun getPostDetails(id: Int, force: Boolean = false): Flow<Outcome<Post>>
}
