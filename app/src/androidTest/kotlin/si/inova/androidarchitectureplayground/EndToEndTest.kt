package si.inova.androidarchitectureplayground

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import si.inova.androidarchitectureplayground.instrumentation.TestNetworkUrlModule
import si.inova.androidarchitectureplayground.util.registerStandardIdlingResources
import si.inova.kotlinova.retrofit.mockWebServer
import si.inova.kotlinova.retrofit.setJsonBodyFromResource

class EndToEndTest {
   @get:Rule
   val composeTestRule = createAndroidComposeRule<MainActivity>()

   @Before
   fun setUp() {
      composeTestRule.registerStandardIdlingResources()
   }

   @Test
   fun showListOfProducts() = mockWebServer {
      TestNetworkUrlModule.url = baseUrl

      mockResponse("/products?skip=0") {
         setJsonBodyFromResource("product_list.json")
      }
      mockResponse("/products?skip=2") {
         setJsonBodyFromResource("empty_product_list.json")
      }
      mockResponse("/products?skip=0") {
         setJsonBodyFromResource("product_list.json")
      }

      composeTestRule.onNodeWithText("Products").performClick()
      composeTestRule.onNodeWithText("iPhone 9").assertIsDisplayed()
      composeTestRule.onNodeWithText("iPhone X").assertIsDisplayed()

      mockResponse("/products?skip=0") {
         setJsonBodyFromResource("product_list_2.json")
      }

      composeTestRule.onNodeWithText("Refresh").performClick()
      composeTestRule.onNodeWithText("iPhone 10").assertIsDisplayed()
      composeTestRule.onNodeWithText("iPhone Z").assertIsDisplayed()
   }
}
