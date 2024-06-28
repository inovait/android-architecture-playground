package si.inova.androidarchitectureplayground.post

import kotlinx.coroutines.flow.Flow
import si.inova.androidarchitectureplayground.common.pagination.PaginatedDataStream
import si.inova.androidarchitectureplayground.post.model.Post
import si.inova.kotlinova.core.outcome.Outcome

interface PostsRepository {
   fun getAllPosts(force: Boolean = false): PaginatedDataStream<List<Post>>
   fun getPostDetails(id: Int, force: Boolean = false): Flow<Outcome<Post>>
}
