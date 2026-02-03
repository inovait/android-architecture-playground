package si.inova.androidarchitectureplayground

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import si.inova.androidarchitectureplayground.instrumentation.IntegrationTestRule
import si.inova.androidarchitectureplayground.util.onAwaitingNodeWithText
import si.inova.kotlinova.retrofit.createJsonMockResponseFromResource
import si.inova.kotlinova.retrofit.runServer

class UsersTest {
   @get:Rule
   val composeTestRule = IntegrationTestRule()

   @Test
   fun showListOfUsers() = composeTestRule.scope.runServer {
      mockResponse("/posts?limit=10&skip=0", includeQueryParameters = true) {
         createJsonMockResponseFromResource("post_list.json")
      }
      mockResponse("/users?limit=30&skip=0", includeQueryParameters = true) {
         createJsonMockResponseFromResource("user_list.json")
      }
      mockResponse("/users?limit=30", includeQueryParameters = true) {
         createJsonMockResponseFromResource("user_list.json")
      }
      mockResponse("/users?limit=10&skip=3", includeQueryParameters = true) {
         createJsonMockResponseFromResource("user_list_empty.json")
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
         createJsonMockResponseFromResource("post_list.json")
      }
      mockResponse("/users?limit=30&skip=0", includeQueryParameters = true) {
         createJsonMockResponseFromResource("user_list.json")
      }
      mockResponse("/users?limit=30", includeQueryParameters = true) {
         createJsonMockResponseFromResource("user_list.json")
      }
      mockResponse("/users?limit=10&skip=3", includeQueryParameters = true) {
         createJsonMockResponseFromResource("user_list_empty.json")
      }

      mockResponse("/users/3") {
         createJsonMockResponseFromResource("single_user.json")
      }

      composeTestRule.onAwaitingNodeWithText("Login").performClick()
      composeTestRule.onAwaitingNodeWithText("Users").performClick()
      composeTestRule.onAwaitingNodeWithText("Max Howell").performClick()

      composeTestRule.onAwaitingNodeWithText("Apache Helicopter", substring = true).assertIsDisplayed()
   }
}
