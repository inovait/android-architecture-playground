package si.inova.androidarchitectureplayground.user.ui.list

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import io.kotest.matchers.collections.shouldContainExactly
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import si.inova.androidarchitectureplayground.navigation.keys.UserDetailsScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.UserListScreenKey
import si.inova.androidarchitectureplayground.paging.pagedListOf
import si.inova.androidarchitectureplayground.ui.theme.AndroidArchitecturePlaygroundTheme
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.test.FakeNavigator

class UserListScreenTest {
   @get:Rule
   val rule = createComposeRule()

   private val viewModel = FakeUserListViewModel()
   private val navigator = FakeNavigator(UserListScreenKey)
   private val screen = UserListScreen(viewModel, navigator)

   @Before
   fun setUp() {
      viewModel.userList.value = Outcome.Success(
         pagedListOf(
            List(10) {
               User(
                  id = it,
                  firstName = "John",
                  lastName = "Smith $it",
               )
            }
         )
      )
   }

   @Test
   fun showData() {
      rule.setContent {
         AndroidArchitecturePlaygroundTheme {
            screen.Content(UserListScreenKey)
         }
      }
      rule.waitUntil {
         rule.onNodeWithText("John Smith 1").isDisplayed()
      }

      rule.onNodeWithText("John Smith 1").assertIsDisplayed()
      rule.onNodeWithText("John Smith 2").assertIsDisplayed()
      rule.onNodeWithText("John Smith 3").assertIsDisplayed()
      rule.onNodeWithText("John Smith 4").assertIsDisplayed()
   }

   @Test
   fun pullToRefresh() {
      rule.setContent {
         AndroidArchitecturePlaygroundTheme {
            screen.Content(UserListScreenKey)
         }
      }
      rule.waitUntil {
         rule.onNodeWithText("John Smith 1").isDisplayed()
      }

      rule.waitForIdle()
      viewModel.refreshCalled = false

      rule.onNodeWithText("John Smith 4").performTouchInput {
         swipeDown(endY = bottom * 5)
      }

      rule.waitForIdle()

      viewModel.refreshCalled = true
   }

   @Test
   fun openUserDetails() {
      rule.setContent {
         AndroidArchitecturePlaygroundTheme {
            screen.Content(UserListScreenKey)
         }
      }
      rule.waitUntil {
         rule.onNodeWithText("John Smith 1").isDisplayed()
      }

      rule.onNodeWithText("John Smith 3").performClick()

      navigator.backstack.shouldContainExactly(
         UserListScreenKey,
         UserDetailsScreenKey(3)
      )
   }
}
