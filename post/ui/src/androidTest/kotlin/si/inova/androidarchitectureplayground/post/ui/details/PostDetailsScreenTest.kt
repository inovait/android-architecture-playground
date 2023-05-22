package si.inova.androidarchitectureplayground.post.ui.details

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import coil.Coil
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.MainScope
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import si.inova.androidarchitectureplaygroud.post.model.Post
import si.inova.androidarchitectureplayground.navigation.keys.PostDetailsScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.UserDetailsScreenKey
import si.inova.androidarchitectureplayground.post.FakePostsRepository
import si.inova.androidarchitectureplayground.ui.theme.AndroidArchitecturePlaygroundTheme
import si.inova.kotlinova.compose.preview.FakeCoilLoader
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.test.FakeNavigator

class PostDetailsScreenTest {
   @get:Rule
   val rule = createComposeRule()

   private val postRepository = FakePostsRepository()
   private val navigator = FakeNavigator(PostDetailsScreenKey(77))

   private val viewModel = PostDetailsViewModel(CoroutineResourceManager(MainScope(), { throw it }), postRepository)
   private val screen = PostDetailsScreen(viewModel, navigator)

   @Before
   fun setUp() {
      Coil.setImageLoader(FakeCoilLoader())

      postRepository.setPostDetails(
         77,
         Outcome.Success(
            Post(
               id = 11,
               title = "She was aware that things could go wrong.",
               body =
               "She was aware that things could go wrong. In fact, she had trained her entire life in anticipation that...",
               userId = 26,
               tags = listOf("love", "english"),
               numReactions = 7,
               image = "https://i.dummyjson.com/data/products/12/1.jpg"
            )
         )
      )
   }

   @After
   fun tearDown() {
      Coil.reset()
   }

   @Test
   fun showData() {
      rule.setContent {
         AndroidArchitecturePlaygroundTheme {
            screen.Content(PostDetailsScreenKey(77))
         }
      }

      rule.onNodeWithText("In fact, she had trained her entire life in anticipation that", substring = true).assertIsDisplayed()
   }

   @Test
   fun pullToRefresh() {
      rule.setContent {
         AndroidArchitecturePlaygroundTheme {
            screen.Content(PostDetailsScreenKey(77))
         }
      }

      rule.waitForIdle()
      postRepository.numTimesForceLoadCalled shouldBe 0

      rule.onNodeWithText("She was aware that things could go wrong.", substring = true).performTouchInput {
         swipeDown(endY = bottom * 5)
      }
      rule.waitForIdle()

      postRepository.numTimesForceLoadCalled shouldBe 1
   }

   @Test
   fun openPostAuthor() {
      rule.setContent {
         AndroidArchitecturePlaygroundTheme {
            screen.Content(PostDetailsScreenKey(77))
         }
      }

      rule.onNodeWithText("Open author").performClick()

      navigator.backstack.shouldContainExactly(
         PostDetailsScreenKey(77),
         UserDetailsScreenKey(26)
      )
   }
}
