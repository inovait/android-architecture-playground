package si.inova.androidarchitectureplayground.post.ui.list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.MainScope
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import si.inova.androidarchitectureplaygroud.post.model.Post
import si.inova.androidarchitectureplayground.common.pagination.PaginatedDataStream
import si.inova.androidarchitectureplayground.navigation.keys.PostListScreenKey
import si.inova.androidarchitectureplayground.post.FakePostsRepository
import si.inova.androidarchitectureplayground.ui.theme.AndroidArchitecturePlaygroundTheme
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.test.FakeNavigator

class PostListScreenTest {
   @get:Rule
   val rule = createComposeRule()

   private val repository = FakePostsRepository()
   private val viewModel = PostListViewModel(CoroutineResourceManager(MainScope(), { throw it }), repository)
   private val navigator = FakeNavigator(PostListScreenKey)
   private val screen = PostListScreen(viewModel, navigator)

   @Before
   fun setUp() {
      repository.setPostList(
         PaginatedDataStream.PaginationResult(
            items = Outcome.Success(
               List(10) {
                  Post(
                     id = it,
                     title = "A post $it",
                  )
               }
            ),
            hasAnyDataLeft = true
         )
      )

      viewModel.onServiceRegistered()
   }

   @Test
   fun showData() {
      rule.setContent {
         AndroidArchitecturePlaygroundTheme {
            screen.Content(PostListScreenKey)
         }
      }

      rule.onNodeWithText("A post 1").assertIsDisplayed()
      rule.onNodeWithText("A post 2").assertIsDisplayed()
      rule.onNodeWithText("A post 3").assertIsDisplayed()
      rule.onNodeWithText("A post 4").assertIsDisplayed()
   }

   @Test
   fun loadMoreDataWhenScrollingToBottom() {
      rule.setContent {
         AndroidArchitecturePlaygroundTheme {
            screen.Content(PostListScreenKey)
         }
      }

      rule.waitForIdle()
      repository.numTimesNextPageCalled shouldBe 0

      rule.onNodeWithText("A post 4").performTouchInput {
         swipeUp()
      }

      repository.numTimesNextPageCalled shouldBe 1
   }

   @Test
   fun pullToRefresh() {
      rule.setContent {
         AndroidArchitecturePlaygroundTheme {
            screen.Content(PostListScreenKey)
         }
      }

      rule.waitForIdle()
      repository.numTimesForceLoadCalled shouldBe 0

      rule.onNodeWithText("A post 4").performTouchInput {
         swipeDown(endY = bottom * 5)
      }

      rule.waitForIdle()

      repository.numTimesForceLoadCalled shouldBe 1
   }

   @Test
   fun openPostDetails() {
      rule.setContent {
         AndroidArchitecturePlaygroundTheme {
            screen.Content(PostListScreenKey)
         }
      }

      rule.onNodeWithText("A post 3").performClick()

//      navigator.backstack.shouldContainExactly(
//         PostListScreenKey,
//         PostDetailsScreenKey(3)
//      )
   }
}
