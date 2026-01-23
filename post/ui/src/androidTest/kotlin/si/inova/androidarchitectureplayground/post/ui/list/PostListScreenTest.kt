package si.inova.androidarchitectureplayground.post.ui.list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import androidx.test.platform.app.InstrumentationRegistry
import com.zhuinden.simplestack.Backstack
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.MainScope
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import si.inova.androidarchitectureplayground.common.pagination.PaginatedDataStream
import si.inova.androidarchitectureplayground.navigation.keys.PostDetailsScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.PostListScreenKey
import si.inova.androidarchitectureplayground.post.FakePostsRepository
import si.inova.androidarchitectureplayground.post.model.Post
import si.inova.androidarchitectureplayground.ui.theme.AndroidArchitecturePlaygroundTheme
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.test.FakeNavigator

class PostListScreenTest {
   @get:Rule
   val rule = createComposeRule()

   private val repository = FakePostsRepository()
   private val viewModel = PostListViewModel(CoroutineResourceManager(MainScope(), { throw it }), repository, {})
   private val navigator = FakeNavigator(PostListScreenKey)
   private lateinit var screen: PostListScreen

   @Before
   fun setUp() {
      InstrumentationRegistry.getInstrumentation().runOnMainSync {
         // Backstack must be created on the main thread
         screen = PostListScreen(viewModel, navigator, Backstack().apply { setup(listOf(PostListScreenKey)) })
      }

      repository.setPostList(
         PaginatedDataStream.PaginationResult(
            items = Outcome.Success(
               List(20) { index ->
                  Post(
                     id = index,
                     title = "A post $index",
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

      repeat(2) {
         rule.onRoot().performTouchInput {
            swipeUp()
         }
      }

      rule.waitForIdle()

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

      navigator.backstack.shouldContainExactly(
         PostListScreenKey,
         PostDetailsScreenKey(3)
      )
   }
}
