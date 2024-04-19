package si.inova.androidarchitectureplayground.user.ui.list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import androidx.compose.ui.test.swipeUp
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.MainScope
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import si.inova.androidarchitectureplayground.common.pagination.PaginatedDataStream
import si.inova.androidarchitectureplayground.navigation.keys.UserDetailsScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.UserListScreenKey
import si.inova.androidarchitectureplayground.ui.theme.AndroidArchitecturePlaygroundTheme
import si.inova.androidarchitectureplayground.user.FakeUserRepository
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.test.FakeNavigator

class UserListScreenTest {
   @get:Rule
   val rule = createComposeRule()

   private val repository = FakeUserRepository()
   private val viewModel = UserListViewModel(CoroutineResourceManager(MainScope(), { throw it }), repository)
   private val navigator = FakeNavigator(UserListScreenKey)
   private val screen = UserListScreen(viewModel, navigator)

   @Before
   fun setUp() {
      repository.setUserList(
         PaginatedDataStream.PaginationResult(
            items = Outcome.Success(
               List(10) {
                  User(
                     id = it,
                     firstName = "John",
                     lastName = "Smith $it",
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
            screen.Content(UserListScreenKey)
         }
      }

      rule.onNodeWithText("John Smith 1").assertIsDisplayed()
      rule.onNodeWithText("John Smith 2").assertIsDisplayed()
      rule.onNodeWithText("John Smith 3").assertIsDisplayed()
      rule.onNodeWithText("John Smith 4").assertIsDisplayed()
   }

   @Test
   fun loadMoreDataWhenScrollingToBottom() {
      rule.setContent {
         AndroidArchitecturePlaygroundTheme {
            screen.Content(UserListScreenKey)
         }
      }

      rule.waitForIdle()
      repository.numTimesNextPageCalled shouldBe 0

      rule.onNodeWithText("John Smith 4").performTouchInput {
         swipeUp()
      }
      rule.waitForIdle()

      repository.numTimesNextPageCalled shouldBe 1
   }

   @Test
   fun pullToRefresh() {
      rule.setContent {
         AndroidArchitecturePlaygroundTheme {
            screen.Content(UserListScreenKey)
         }
      }

      rule.waitForIdle()
      repository.numTimesForceLoadCalled shouldBe 0

      rule.onNodeWithText("John Smith 4").performTouchInput {
         swipeDown(endY = bottom * 5)
      }

      rule.waitForIdle()

      repository.numTimesForceLoadCalled shouldBe 1
   }

   @Test
   fun openUserDetails() {
      rule.setContent {
         AndroidArchitecturePlaygroundTheme {
            screen.Content(UserListScreenKey)
         }
      }

      rule.onNodeWithText("John Smith 3").performClick()

      navigator.backstack.shouldContainExactly(
         UserListScreenKey,
         UserDetailsScreenKey(3)
      )
   }
}
