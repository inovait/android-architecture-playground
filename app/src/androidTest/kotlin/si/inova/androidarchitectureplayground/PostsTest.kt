package si.inova.androidarchitectureplayground

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import si.inova.androidarchitectureplayground.instrumentation.IntegrationTestRule
import si.inova.androidarchitectureplayground.util.onAwaitingNodeWithText
import si.inova.kotlinova.retrofit.runServer
import si.inova.kotlinova.retrofit.setJsonBodyFromResource

@OptIn(ExperimentalTestApi::class)
class PostsTest {
   @get:Rule
   val composeTestRule = IntegrationTestRule()

   @Test
   fun showListOfPosts() = composeTestRule.scope.runServer {
      mockResponse("/posts?limit=10&skip=0", includeQueryParameters = true) {
         setJsonBodyFromResource("post_list.json")
      }
      mockResponse("/users?limit=30&skip=0", includeQueryParameters = true) {
         setJsonBodyFromResource("user_list.json")
      }

      composeTestRule.onAwaitingNodeWithText("Login").performClick()
      composeTestRule.onAwaitingNodeWithText("Posts").performClick()

      composeTestRule.onAwaitingNodeWithText("His mother had always taught him").assertIsDisplayed()
      composeTestRule.onAwaitingNodeWithText("He was an expert but not in a discipline").assertIsDisplayed()
   }

   @Test
   fun showPostDetails() = composeTestRule.scope.runServer {
      mockResponse("/posts?limit=10&skip=0", includeQueryParameters = true) {
         setJsonBodyFromResource("post_list.json")
      }
      mockResponse("/users?limit=30&skip=0", includeQueryParameters = true) {
         setJsonBodyFromResource("user_list.json")
      }
      mockResponse("/posts/2") {
         setJsonBodyFromResource("single_post.json")
      }

      composeTestRule.onAwaitingNodeWithText("Login").performClick()
      composeTestRule.onAwaitingNodeWithText("Posts").performClick()
      composeTestRule.onAwaitingNodeWithText("He was an expert but not in a discipline").performClick()

      composeTestRule.onAwaitingNodeWithText("love", substring = true).assertIsDisplayed()
   }
}
