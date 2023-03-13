package si.inova.androidarchitectureplayground.products.ui

import androidx.compose.ui.test.junit4.createComposeRule
import com.karumi.shot.ScreenshotTest
import org.junit.Rule
import org.junit.Test
import si.inova.androidarchitectureplayground.common.exceptions.NoNetworkException
import si.inova.androidarchitectureplayground.common.outcome.Outcome
import si.inova.androidarchitectureplayground.screens.ProductListScreenKey

class ProductListScreenshotTest : ScreenshotTest {
   @get:Rule
   val composeTestRule = createComposeRule()

   private val viewModel = FakeProductListViewModel()
   private val screen = ProductListScreen(viewModel)

   @Test
   fun productListSnapshot() {
      composeTestRule.setContent {
         screen.Content(ProductListScreenKey)
      }

      viewModel.products.value = Outcome.Success(
         listOf(
            fakeProduct("A"),
            fakeProduct("B"),
            fakeProduct("C")
         )
      )

      compareScreenshot(composeTestRule)
   }

   @Test
   fun productListLoadingSnapshot() {
      composeTestRule.setContent {
         screen.Content(ProductListScreenKey)
      }

      viewModel.products.value = Outcome.Progress(
         listOf(
            fakeProduct("A"),
            fakeProduct("B"),
            fakeProduct("C")
         )
      )

      compareScreenshot(composeTestRule)
   }

   @Test
   fun errorSnapshot() {
      composeTestRule.setContent {
         screen.Content(ProductListScreenKey)
      }

      viewModel.products.value = Outcome.Error(
         NoNetworkException(),
         listOf(
            fakeProduct("A"),
            fakeProduct("B"),
            fakeProduct("C")
         )
      )

      compareScreenshot(composeTestRule)
   }
}
