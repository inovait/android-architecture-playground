package si.inova.androidarchitectureplayground.post.ui.list

import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import si.inova.androidarchitectureplaygroud.post.model.Post
import si.inova.androidarchitectureplayground.common.pagination.PaginatedDataStream
import si.inova.androidarchitectureplayground.post.FakePostsRepository
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.core.test.outcomes.shouldBeSuccessWithData
import si.inova.kotlinova.core.test.outcomes.testCoroutineResourceManager

class PostListViewModelTest {
   private val scope = TestScope()
   private val repository = FakePostsRepository()
   private val resources = scope.testCoroutineResourceManager()

   private val viewModel = PostListViewModel(resources, repository)

   @Test
   fun `Load users`() = scope.runTest {
      repository.setPostList(
         PaginatedDataStream.PaginationResult(
            items = Outcome.Success(
               listOf(
                  Post(
                     id = 1,
                     title = "A post 1",
                  ),
                  Post(
                     id = 2,
                     title = "A post 2",
                  )
               )
            ),
            hasAnyDataLeft = true
         )
      )

      viewModel.onServiceRegistered()

      viewModel.postList.test {
         runCurrent()

         expectMostRecentItem() shouldBeSuccessWithData PostListState(
            posts = listOf(
               Post(
                  id = 1,
                  title = "A post 1",
               ),
               Post(
                  id = 2,
                  title = "A post 2",
               )
            ),
            hasAnyDataLeft = true
         )
      }
   }

   @Test
   fun `Load next page`() = scope.runTest {
      repository.setPostList(
         PaginatedDataStream.PaginationResult(
            items = Outcome.Success(
               listOf(
                  Post(
                     id = 1,
                     title = "A post 1",
                  ),
                  Post(
                     id = 2,
                     title = "A post 2",
                  )
               )
            ),
            hasAnyDataLeft = true
         )
      )

      viewModel.onServiceRegistered()

      viewModel.postList.test {
         runCurrent()

         viewModel.nextPage()
         runCurrent()

         repository.numTimesNextPageCalled shouldBe 1
         cancelAndConsumeRemainingEvents()
      }
   }
}
