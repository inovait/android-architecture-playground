package si.inova.androidarchitectureplaygroud.post

import si.inova.androidarchitectureplaygroud.post.model.Post
import si.inova.androidarchitectureplayground.common.pagination.PaginatedDataStream

interface PostsRepository {
   fun getAllPosts(force: Boolean = false): PaginatedDataStream<List<Post>>
   suspend fun getPostDetails(id: Int, force: Boolean = false): Post
}
