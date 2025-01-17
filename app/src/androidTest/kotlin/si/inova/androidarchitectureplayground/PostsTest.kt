package si.inova.androidarchitectureplayground

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Rule
import org.junit.Test
import si.inova.androidarchitectureplayground.instrumentation.IntegrationTestRule
import si.inova.kotlinova.retrofit.runServer
import si.inova.kotlinova.retrofit.setJsonBodyFromResource

class PostsTest {
   @get:Rule
   val composeTestRule = IntegrationTestRule()

   @Test
   fun showListOfPosts() = composeTestRule.scope.runServer {
      mockResponse("/posts?limit=10&skip=0", includeQueryParameters = true) {
         setJsonBodyFromResource("post_list.json")
      }
      mockResponse("/users?limit=10&skip=0", includeQueryParameters = true) {
         setJsonBodyFromResource("user_list.json")
      }

      composeTestRule.onNodeWithText("Login").performClick()
      composeTestRule.onNodeWithText("Posts").performClick()

      composeTestRule.onNodeWithText("His mother had always taught him").assertIsDisplayed()
      composeTestRule.onNodeWithText("He was an expert but not in a discipline").assertIsDisplayed()
   }

   @Test
   fun showPostDetails() = composeTestRule.scope.runServer {
      mockResponse("/posts?limit=10&skip=0", includeQueryParameters = true) {
         setJsonBodyFromResource("post_list.json")
      }
      mockResponse("/users?limit=10&skip=0", includeQueryParameters = true) {
         setJsonBodyFromResource("user_list.json")
      }
      mockResponse("/posts/2") {
         setJsonBodyFromResource("single_post.json")
      }

      composeTestRule.onNodeWithText("Login").performClick()
      composeTestRule.onNodeWithText("Posts").performClick()
      composeTestRule.onNodeWithText("He was an expert but not in a discipline").performClick()

      composeTestRule.onNodeWithText("love", substring = true).assertIsDisplayed()
   }
}
