package si.inova.androidarchitectureplayground.post.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import si.inova.androidarchitectureplayground.post.network.model.PostDto
import si.inova.androidarchitectureplayground.post.network.model.PostsDto

interface PostsService {
   @GET("posts")
   suspend fun getPosts(
      @Query("limit")
      limit: Int? = null,
      @Query("skip")
      skip: Int? = null,
   ): PostsDto

   @GET("posts/{id}")
   suspend fun getPost(
      @Path("id")
      id: Int,
   ): PostDto
}
