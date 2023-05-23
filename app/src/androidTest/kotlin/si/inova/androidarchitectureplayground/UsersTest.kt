package si.inova.androidarchitectureplayground

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import si.inova.androidarchitectureplayground.instrumentation.IntegrationTestRule
import si.inova.kotlinova.retrofit.setJsonBodyFromResource

class UsersTest {
   @get:Rule
   val composeTestRule = IntegrationTestRule()

   @Test
   fun showListOfUsers() = composeTestRule.runWithServer {
      mockResponse("/posts?limit=10&skip=0") {
         setJsonBodyFromResource("post_list.json")
      }
      mockResponse("/users?limit=10&skip=0") {
         setJsonBodyFromResource("user_list.json")
      }

      composeTestRule.onNodeWithText("Login").performClick()
      composeTestRule.onNodeWithText("Users").performClick()

      composeTestRule.onNodeWithText("Terry Medhurst").assertIsDisplayed()
      composeTestRule.onNodeWithText("Juliet Beck").assertIsDisplayed()
      composeTestRule.onNodeWithText("Max Howell").assertIsDisplayed()
   }

   @Test
   fun showUserDetails() = composeTestRule.runWithServer {
      mockResponse("/posts?limit=10&skip=0") {
         setJsonBodyFromResource("post_list.json")
      }
      mockResponse("/users?limit=10&skip=0") {
         setJsonBodyFromResource("user_list.json")
      }

      mockResponse("/users/3") {
         setJsonBodyFromResource("single_user.json")
      }

      composeTestRule.onNodeWithText("Login").performClick()
      composeTestRule.onNodeWithText("Users").performClick()
      composeTestRule.onNodeWithText("Max Howell").performClick()

      composeTestRule.onNodeWithText("Apache Helicopter", substring = true).assertIsDisplayed()
   }
}
