package si.inova.androidarchitectureplaygroud.post

import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.turbine.test
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import si.inova.androidarchitectureplaygroud.post.db.createTestPostQueries
import si.inova.androidarchitectureplaygroud.post.exceptions.UnknownPostException
import si.inova.androidarchitectureplaygroud.post.model.Post
import si.inova.androidarchitectureplaygroud.post.network.FakePostsService
import si.inova.androidarchitectureplaygroud.post.network.model.LightPostDto
import si.inova.androidarchitectureplaygroud.post.network.model.PostDto
import si.inova.androidarchitectureplayground.common.test.datastore.runTestWithDispatchers
import si.inova.androidarchitectureplayground.network.exceptions.BackendException
import si.inova.kotlinova.core.exceptions.NoNetworkException
import si.inova.kotlinova.core.test.outcomes.shouldBeErrorWith
import si.inova.kotlinova.core.test.outcomes.shouldBeProgressWithData
import si.inova.kotlinova.core.test.outcomes.shouldBeSuccessWithData
import si.inova.kotlinova.core.test.time.virtualTimeProvider
import si.inova.kotlinova.retrofit.InterceptionStyle
import java.util.concurrent.TimeUnit

class PostsRepositoryImplTest {
   private val postService = FakePostsService()
   private val postQueries = createTestPostQueries()
   private val scope = TestScope()

   private val repository = PostsRepositoryImpl(postService, postQueries, scope.virtualTimeProvider())

   @BeforeEach
   fun setUp() {
      postService.providedPosts = createFakePostsDto(0, 30)
   }

   @Test
   fun `Provide first page of list of posts from network`() = scope.runTestWithDispatchers {
      val expectedPosts = createFakePosts(0, 10)

      val dataStream = repository.getAllPosts()
      dataStream.data.test {
         runCurrent()
         val posts = expectMostRecentItem()

         posts.items shouldBeSuccessWithData expectedPosts
      }
   }

   @Test
   fun `Provide second page of list of posts from network`() = scope.runTestWithDispatchers {
      val expectedPosts = createFakePosts(0, 20)

      val dataStream = repository.getAllPosts()
      dataStream.data.test {
         runCurrent()
         dataStream.nextPage()
         runCurrent()

         val posts = expectMostRecentItem()

         posts.items shouldBeSuccessWithData expectedPosts
      }
   }

   @Test
   fun `Provide first page from cached database`() = scope.runTestWithDispatchers {
      val expectedPosts = createFakePosts(0, 10)

      val cachingDataStream = repository.getAllPosts()
      cachingDataStream.data.first() // Load data into database

      postService.interceptAllFutureCallsWith(InterceptionStyle.Error(IllegalStateException("Network should not be called now")))

      val dataStream = repository.getAllPosts()

      dataStream.data.test {
         runCurrent()

         val posts = expectMostRecentItem()

         posts.items shouldBeSuccessWithData expectedPosts
      }
   }

   @Test
   fun `Provide single post from network`() = scope.runTestWithDispatchers {
      postService.providePostDetails(
         PostDto(
            id = 1,
            title = "She was aware that things could go wrong.",
            body = "She was aware that things could go wrong. In fact, she had trained her entire life in anticipation that...",
            userId = 26,
            tags = listOf("love", "english"),
            reactions = PostDto.Reactions(7, 0),
            image = "https://i.dummyjson.com/data/products/12/1.jpg"
         )
      )

      val expectedPost = Post(
         id = 1,
         title = "She was aware that things could go wrong.",
         body = "She was aware that things could go wrong. In fact, she had trained her entire life in anticipation that...",
         userId = 26,
         tags = listOf("love", "english"),
         numReactions = 7,
         image = "https://i.dummyjson.com/data/products/12/1.jpg"
      )

      val post = repository.getPostDetails(1)
      post.test {
         awaitItem() shouldBeSuccessWithData expectedPost
         cancelAndConsumeRemainingEvents()
      }
   }

   @Test
   fun `Provide proper error message when post does not exist`() = scope.runTestWithDispatchers {
      postService.interceptAllFutureCallsWith(
         InterceptionStyle.Error(BackendException(backendMessage = "Post with id '7' not found", cause = NoNetworkException()))
      )

      val post = repository.getPostDetails(1)
      post.test {
         awaitItem().shouldBeErrorWith(exceptionType = UnknownPostException::class.java)

         cancelAndConsumeRemainingEvents()
      }
   }

   @Test
   fun `Provide single post from cached database`() = scope.runTestWithDispatchers {
      postService.providePostDetails(
         PostDto(
            id = 1,
            title = "She was aware that things could go wrong.",
            body = "She was aware that things could go wrong. In fact, she had trained her entire life in anticipation that...",
            userId = 26,
            tags = listOf("love", "english"),
            reactions = PostDto.Reactions(0, 7),
            image = "https://i.dummyjson.com/data/products/12/1.jpg"
         )
      )

      val expectedPost = Post(
         id = 1,
         title = "She was aware that things could go wrong.",
         body = "She was aware that things could go wrong. In fact, she had trained her entire life in anticipation that...",
         userId = 26,
         tags = listOf("love", "english"),
         numReactions = 7,
         image = "https://i.dummyjson.com/data/products/12/1.jpg"
      )

      repository.getPostDetails(1).first() // Load data into database

      postService.interceptAllFutureCallsWith(InterceptionStyle.Error(IllegalStateException("Network should not be called now")))

      val post = repository.getPostDetails(1)
      post.test {
         awaitItem() shouldBeSuccessWithData expectedPost
         cancelAndConsumeRemainingEvents()
      }
   }

   @Test
   fun `Update post when it changes in the database`() = scope.runTestWithDispatchers {
      postService.providePostDetails(
         PostDto(
            id = 1,
            title = "She was aware that things could go wrong.",
            body = "She was aware that things could go wrong. In fact, she had trained her entire life in anticipation that...",
            userId = 26,
            tags = listOf("love", "english"),
            reactions = PostDto.Reactions(3, 4),
            image = "https://i.dummyjson.com/data/products/12/1.jpg"
         )
      )

      val expectedPost = Post(
         id = 1,
         title = "She was aware that things could go wrong.",
         body = "She was aware that things could go wrong. In fact, she had trained her entire life in anticipation that...",
         userId = 26,
         tags = listOf("love", "german"),
         numReactions = 7,
         image = "https://i.dummyjson.com/data/products/12/1.jpg"
      )

      repository.getPostDetails(1).first() // Load data into database

      postService.interceptAllFutureCallsWith(InterceptionStyle.Error(IllegalStateException("Network should not be called now")))

      val post = repository.getPostDetails(1)
      post.test {
         runCurrent()

         val dbPost = postQueries.selectSingle(1).awaitAsOne().copy(tags = "love,german")
         postQueries.insert(dbPost)
         runCurrent()

         expectMostRecentItem() shouldBeSuccessWithData expectedPost
         cancelAndConsumeRemainingEvents()
      }
   }

   @Test
   fun `When cache for post details is expired, return expired data first as Loading while fetching in the background`() {
      val testPost = PostDto(
         id = 1,
         title = "She was aware that things could go wrong.",
         body = "She was aware that things could go wrong. In fact, she had trained her entire life in anticipation that...",
         userId = 26,
         tags = listOf("love", "english"),
         reactions = PostDto.Reactions(7, 0),
         image = "https://i.dummyjson.com/data/products/12/1.jpg"
      )

      scope.runTestWithDispatchers {
         postService.providePostDetails(testPost)

         val expectedPostLoading = Post(
            id = 1,
            title = "She was aware that things could go wrong.",
            body = "She was aware that things could go wrong. In fact, she had trained her entire life in anticipation that...",
            userId = 26,
            tags = listOf("love", "english"),
            numReactions = 7,
            image = "https://i.dummyjson.com/data/products/12/1.jpg"
         )

         val expectedPostSuccess = expectedPostLoading.copy(numReactions = 12)

         repository.getPostDetails(1).test {
            // Load data into database cache
            runCurrent()
            cancelAndConsumeRemainingEvents()
         }

         postService.providePostDetails(testPost.copy(reactions = PostDto.Reactions(6, 6)))

         advanceTimeBy(TimeUnit.MINUTES.toMillis(15))

         repository.getPostDetails(1).test {
            awaitItem() shouldBeProgressWithData expectedPostLoading
            awaitItem() shouldBeSuccessWithData expectedPostSuccess
         }
      }
   }

   @Test
   fun `When force loading, return expired data first as Loading while fetching in the background`() {
      val testPost = PostDto(
         id = 1,
         title = "She was aware that things could go wrong.",
         body = "She was aware that things could go wrong. In fact, she had trained her entire life in anticipation that...",
         userId = 26,
         tags = listOf("love", "english"),
         reactions = PostDto.Reactions(7, 0),
         image = "https://i.dummyjson.com/data/products/12/1.jpg"
      )

      scope.runTestWithDispatchers {
         postService.providePostDetails(testPost)

         val expectedPostLoading = Post(
            id = 1,
            title = "She was aware that things could go wrong.",
            body = "She was aware that things could go wrong. In fact, she had trained her entire life in anticipation that...",
            userId = 26,
            tags = listOf("love", "english"),
            numReactions = 7,
            image = "https://i.dummyjson.com/data/products/12/1.jpg"
         )

         val expectedPostSuccess = expectedPostLoading.copy(numReactions = 8)

         repository.getPostDetails(1).test {
            // Load data into database cache
            runCurrent()
            cancelAndConsumeRemainingEvents()
         }

         postService.providePostDetails(testPost.copy(reactions = PostDto.Reactions(2, 6)))

         runCurrent()

         repository.getPostDetails(1, force = true).test {
            awaitItem() shouldBeProgressWithData expectedPostLoading
            awaitItem() shouldBeSuccessWithData expectedPostSuccess
         }
      }
   }

   @Test
   fun `When there is partial post data in db, return partial data first as Loading while fetching in the background`() {
      scope.runTestWithDispatchers {
         postService.providePostDetails(
            PostDto(
               id = 1,
               title = "She was aware that things could go wrong.",
               body =
               "She was aware that things could go wrong. In fact, she had trained her entire life in anticipation that...",
               userId = 26,
               tags = listOf("love", "english"),
               reactions = PostDto.Reactions(7, 0),
               image = "https://i.dummyjson.com/data/products/12/1.jpg"
            )
         )

         val expectedPostLoading = Post(
            id = 1,
            title = "Title 1",
         )

         val expectedPostSuccess = Post(
            id = 1,
            title = "She was aware that things could go wrong.",
            body = "She was aware that things could go wrong. In fact, she had trained her entire life in anticipation that...",
            userId = 26,
            tags = listOf("love", "english"),
            numReactions = 7,
            image = "https://i.dummyjson.com/data/products/12/1.jpg"
         )

         val cachingDataStream = repository.getAllPosts()
         cachingDataStream.data.first() // Load data into database
         runCurrent()

         repository.getPostDetails(1).test {
            awaitItem() shouldBeProgressWithData expectedPostLoading
            awaitItem() shouldBeSuccessWithData expectedPostSuccess
         }
      }
   }

   @Test
   fun `Include stale database data in error, when network fetch of post details fails`() =
      scope.runTestWithDispatchers {
         postService.providePostDetails(
            PostDto(
               id = 1,
               title = "She was aware that things could go wrong.",
               body =
               "She was aware that things could go wrong. In fact, she had trained her entire life in anticipation that...",
               userId = 26,
               tags = listOf("love", "english"),
               reactions = PostDto.Reactions(7, 0),
               image = "https://i.dummyjson.com/data/products/12/1.jpg"
            )
         )

         val expectedPostError = Post(
            id = 1,
            title = "She was aware that things could go wrong.",
            body = "She was aware that things could go wrong. In fact, she had trained her entire life in anticipation that...",
            userId = 26,
            tags = listOf("love", "english"),
            numReactions = 7,
            image = "https://i.dummyjson.com/data/products/12/1.jpg"
         )

         repository.getPostDetails(1).test {
            // Load data into database cache
            runCurrent()
            cancelAndConsumeRemainingEvents()
         }

         postService.interceptAllFutureCallsWith(InterceptionStyle.Error(NoNetworkException()))

         repository.getPostDetails(1, force = true).test {
            awaitItem() // Progress item

            awaitItem().shouldBeErrorWith(
               expectedData = expectedPostError,
               exceptionType = NoNetworkException::class.java
            )

            awaitComplete()
         }
      }
}

private fun createFakePostsDto(from: Int, to: Int): List<LightPostDto> {
   return List(to - from) {
      LightPostDto(
         it,
         "Title $it"
      )
   }
}

private fun createFakePosts(from: Int, to: Int): List<Post> {
   return List(to - from) {
      Post(
         id = it,
         title = "Title $it"
      )
   }
}
