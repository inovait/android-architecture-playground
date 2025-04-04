package si.inova.androidarchitectureplayground

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import si.inova.androidarchitectureplayground.instrumentation.IntegrationTestRule
import si.inova.androidarchitectureplayground.util.onAwaitingNodeWithText
import si.inova.kotlinova.retrofit.runServer
import si.inova.kotlinova.retrofit.setJsonBodyFromResource

class UsersTest {
   @get:Rule
   val composeTestRule = IntegrationTestRule()

   @Test
   fun showListOfUsers() = composeTestRule.scope.runServer {
      mockResponse("/posts?limit=10&skip=0", includeQueryParameters = true) {
         setJsonBodyFromResource("post_list.json")
      }
      mockResponse("/users?limit=10&skip=0", includeQueryParameters = true) {
         setJsonBodyFromResource("user_list.json")
      }

      composeTestRule.onAwaitingNodeWithText("Login").performClick()
      composeTestRule.onAwaitingNodeWithText("Users").performClick()

      composeTestRule.onAwaitingNodeWithText("Terry Medhurst").assertIsDisplayed()
      composeTestRule.onAwaitingNodeWithText("Juliet Beck").assertIsDisplayed()
      composeTestRule.onAwaitingNodeWithText("Max Howell").assertIsDisplayed()
   }

   @Test
   fun showUserDetails() = composeTestRule.scope.runServer {
      mockResponse("/posts?limit=10&skip=0", includeQueryParameters = true) {
         setJsonBodyFromResource("post_list.json")
      }
      mockResponse("/users?limit=10&skip=0", includeQueryParameters = true) {
         setJsonBodyFromResource("user_list.json")
      }

      mockResponse("/users/3") {
         setJsonBodyFromResource("single_user.json")
      }

      composeTestRule.onAwaitingNodeWithText("Login").performClick()
      composeTestRule.onAwaitingNodeWithText("Users").performClick()
      composeTestRule.onAwaitingNodeWithText("Max Howell").performClick()

      composeTestRule.onAwaitingNodeWithText("Apache Helicopter", substring = true).assertIsDisplayed()
   }
}
