package si.inova.androidarchitectureplayground.user.ui.details

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeDown
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.MainScope
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import si.inova.androidarchitectureplayground.navigation.keys.UserDetailsScreenKey
import si.inova.androidarchitectureplayground.ui.theme.AndroidArchitecturePlaygroundTheme
import si.inova.androidarchitectureplayground.user.FakeUserRepository
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome

class UserDetailsScreenTest {
   @get:Rule
   val rule = createComposeRule()

   private val userRepository = FakeUserRepository()

   private val viewModel = UserDetailsViewModel(CoroutineResourceManager(MainScope(), { throw it }), userRepository)
   private val screen = UserDetailsScreen(viewModel)

   @Before
   fun setUp() {
      userRepository.setUserDetails(
         77,
         Outcome.Success(
            User(
               id = 77,
               firstName = "John",
               lastName = "Doe",
               maidenName = "Smith",
               age = 25,
               gender = "Apache Helicopter",
               email = "a@b.com",
               phone = "1234567890",
               hair = User.Hair(
                  "brown",
                  "curly"
               ),
            )
         )
      )
   }

   @Test
   fun showData() {
      rule.setContent {
         AndroidArchitecturePlaygroundTheme {
            screen.Content(UserDetailsScreenKey(77))
         }
      }

      rule.onNodeWithText("John Smith Doe", substring = true).assertIsDisplayed()
   }

   @Test
   fun pullToRefresh() {
      rule.setContent {
         AndroidArchitecturePlaygroundTheme {
            screen.Content(UserDetailsScreenKey(77))
         }
      }

      rule.waitForIdle()
      userRepository.numTimesForceLoadCalled shouldBe 0

      rule.onNodeWithText("John Smith", substring = true).performTouchInput {
         swipeDown(endY = bottom * 5)
      }
      rule.waitForIdle()

      userRepository.numTimesForceLoadCalled shouldBe 1
   }
}
