package si.inova.androidarchitectureplayground.post.network

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import si.inova.androidarchitectureplayground.network.exceptions.BackendException
import si.inova.androidarchitectureplayground.network.test.serviceFactory
import si.inova.androidarchitectureplayground.post.PostsDataComponent
import si.inova.androidarchitectureplayground.post.network.model.LightPostDto
import si.inova.androidarchitectureplayground.post.network.model.PostDto
import si.inova.androidarchitectureplayground.post.network.model.PostsDto
import si.inova.kotlinova.retrofit.mockWebServer
import si.inova.kotlinova.retrofit.setJsonBodyFromResource

class PostsServiceTest {
   @Test
   fun `Load post list`() = runTest {
      mockWebServer {
         mockResponse("/posts") {
            setJsonBodyFromResource("post_list.json")
         }

         val serviceFactory = serviceFactory(this@runTest)
         val service = object : PostsDataComponent {}.providePostsService(serviceFactory)

         val expectedPosts = PostsDto(
            posts = listOf(
               LightPostDto(1, "His mother had always taught him"),
               LightPostDto(2, "He was an expert but not in a discipline")
            ),
            total = 2
         )

         val posts = service.getPosts()

         posts shouldBe expectedPosts
      }
   }

   @Test
   fun `Load post list with limit and skip`() = runTest {
      mockWebServer {
         mockResponse("/posts?limit=3&skip=2") {
            setJsonBodyFromResource("post_list.json")
         }

         val serviceFactory = serviceFactory(this@runTest)
         val service = object : PostsDataComponent {}.providePostsService(serviceFactory)

         val expectedPosts = PostsDto(
            posts = listOf(
               LightPostDto(1, "His mother had always taught him"),
               LightPostDto(2, "He was an expert but not in a discipline")
            ),
            total = 2
         )

         val posts = service.getPosts(limit = 3, skip = 2)

         posts shouldBe expectedPosts
      }
   }

   @Test
   fun `Load single post`() = runTest {
      mockWebServer {
         mockResponse("/posts/12") {
            setJsonBodyFromResource("single_post.json")
         }

         val serviceFactory = serviceFactory(this@runTest)
         val service = object : PostsDataComponent {}.providePostsService(serviceFactory)

         val expectedPost = PostDto(
            id = 12,
            title = "She was aware that things could go wrong.",
            body = "She was aware that things could go wrong. In fact, she had trained her entire life in anticipation that...",
            userId = 26,
            tags = listOf("love", "english"),
            reactions = PostDto.Reactions(7, 0),
            image = "https://i.dummyjson.com/data/products/12/1.jpg"
         )

         val post = service.getPost(12)

         post shouldBe expectedPost
      }
   }

   @Test
   fun `Report proper exception when Post does not exist`() = runTest {
      mockWebServer {
         mockResponse("/posts/13") {
            setJsonBodyFromResource("no_post_error.json")
            setResponseCode(404)
         }

         val serviceFactory = serviceFactory(this@runTest)
         val service = object : PostsDataComponent {}.providePostsService(serviceFactory)

         shouldThrow<BackendException> {
            service.getPost(13)
         }.message shouldBe "Post with id '13' not found"
      }
   }
}
