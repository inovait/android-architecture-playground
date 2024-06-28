package si.inova.androidarchitectureplayground.post.network

import si.inova.androidarchitectureplayground.post.network.model.LightPostDto
import si.inova.androidarchitectureplayground.post.network.model.PostDto
import si.inova.androidarchitectureplayground.post.network.model.PostsDto
import si.inova.kotlinova.retrofit.FakeService
import si.inova.kotlinova.retrofit.ServiceTestingHelper

class FakePostsService(private val serviceTestingHelper: ServiceTestingHelper = ServiceTestingHelper()) :
   PostsService, FakeService by serviceTestingHelper {
   var providedPosts: List<LightPostDto>? = null
   private val providedPostDetails: MutableMap<Int, PostDto> = HashMap()
   var lastReceivedLimitSkip: Pair<Int?, Int?>? = null

   fun providePostDetails(details: PostDto) {
      providedPostDetails[details.id] = details
   }

   override suspend fun getPosts(limit: Int?, skip: Int?): PostsDto {
      serviceTestingHelper.intercept()
      lastReceivedLimitSkip = limit to skip
      val providedPosts = providedPosts ?: error("No posts provided")
      val postList = providedPosts.drop(skip ?: 0).take(limit ?: Int.MAX_VALUE)

      return PostsDto(postList, providedPosts.size)
   }

   override suspend fun getPost(id: Int): PostDto {
      serviceTestingHelper.intercept()
      return providedPostDetails[id] ?: error("No post details provided for $id")
   }
}
