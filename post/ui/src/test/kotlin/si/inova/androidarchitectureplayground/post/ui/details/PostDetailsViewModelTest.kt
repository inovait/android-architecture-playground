package si.inova.androidarchitectureplayground.post.ui.details

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import si.inova.androidarchitectureplayground.post.FakePostsRepository
import si.inova.androidarchitectureplayground.post.model.Post
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.core.test.outcomes.shouldBeSuccessWithData
import si.inova.kotlinova.core.test.outcomes.testCoroutineResourceManager

class PostDetailsViewModelTest {
   private val scope = TestScope()
   private val postRepository = FakePostsRepository()

   private val viewModel = PostDetailsViewModel(scope.testCoroutineResourceManager(), postRepository, {})

   @BeforeEach
   fun setUp() {
      postRepository.setPostDetails(17, Outcome.Success(TEST_POST))
   }

   @Test
   fun `Load data`() = scope.runTest {
      viewModel.startLoading(17)

      viewModel.postDetails.test {
         runCurrent()

         expectMostRecentItem() shouldBeSuccessWithData TEST_POST
         postRepository.numTimesForceLoadCalled shouldBe 0
      }
   }

   @Test
   fun `Refresh data`() = scope.runTest {
      viewModel.startLoading(17)
      viewModel.onServiceRegistered()
      viewModel.refresh()

      viewModel.postDetails.test {
         runCurrent()

         postRepository.numTimesForceLoadCalled shouldBe 1
         cancelAndConsumeRemainingEvents()
      }
   }
}

private val TEST_POST = Post(
   id = 11,
   title = "She was aware that things could go wrong.",
   body = "She was aware that things could go wrong. In fact, she had trained her entire life in anticipation that...",
   userId = 26,
   tags = listOf("love", "english"),
   numReactions = 7,
   image = "https://i.dummyjson.com/data/products/12/1.jpg"
)
