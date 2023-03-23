package si.inova.androidarchitectureplayground.products.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test
import si.inova.androidarchitectureplayground.common.outcome.Outcome
import si.inova.androidarchitectureplayground.products.data.model.ProductDto
import si.inova.androidarchitectureplayground.screens.ProductListScreenKey

class ProductListScreenTest {
   @get:Rule
   val composeTestRule = createComposeRule()

   private val viewModel = FakeProductListViewModel()
   private val screen = ProductListScreen(viewModel)

   @Test
   fun showListOfProductsWithSuccess() {
      composeTestRule.setContent {
         screen.Content(ProductListScreenKey)
      }

      viewModel.products.value = Outcome.Success(
         ProductListScreenData(
            listOf(
               fakeProduct("A"),
               fakeProduct("B"),
               fakeProduct("C")
            ),
            true
         )
      )

      composeTestRule.onNodeWithText("A").assertIsDisplayed()
      composeTestRule.onNodeWithText("B").assertIsDisplayed()
      composeTestRule.onNodeWithText("C").assertIsDisplayed()
   }

   @Test
   fun showListOfProductsWhileLoading() {
      composeTestRule.setContent {
         screen.Content(ProductListScreenKey)
      }

      viewModel.products.value = Outcome.Progress(
         ProductListScreenData(
            listOf(
               fakeProduct("A"),
               fakeProduct("B"),
               fakeProduct("C")
            ),
            true
         )
      )

      composeTestRule.onNodeWithText("A").assertIsDisplayed()
      composeTestRule.onNodeWithText("B").assertIsDisplayed()
      composeTestRule.onNodeWithText("C").assertIsDisplayed()
   }

   @Test
   fun showLoaderWhileLoading() {
      composeTestRule.setContent {
         screen.Content(ProductListScreenKey)
      }

      viewModel.products.value = Outcome.Progress(
         ProductListScreenData(
            listOf(
               fakeProduct("A"),
               fakeProduct("B"),
               fakeProduct("C")
            ),
            true
         )
      )

      composeTestRule.onNodeWithTag("loader").assertIsDisplayed()
   }

   @Test
   fun hideLoaderOnSuccess() {
      composeTestRule.setContent {
         screen.Content(ProductListScreenKey)
      }

      viewModel.products.value = Outcome.Success(
         ProductListScreenData(
            listOf(
               fakeProduct("A"),
               fakeProduct("B"),
               fakeProduct("C")
            ),
            true
         )
      )

      composeTestRule.mainClock.advanceTimeBy(600)
      composeTestRule.onNodeWithTag("loader").assertDoesNotExist()
   }

   @Test
   fun refresh() {
      composeTestRule.setContent {
         screen.Content(ProductListScreenKey)
      }

      viewModel.products.value = Outcome.Success(
         ProductListScreenData(
            listOf(
               fakeProduct("A"),
               fakeProduct("B"),
               fakeProduct("C")
            ),
            true
         )
      )

      composeTestRule.onNodeWithText("Refresh").performClick()

      viewModel.refreshCalled shouldBe true
   }
}

fun fakeProduct(name: String): ProductDto {
   return ProductDto(
      "",
      "",
      "",
      0.0,
      0,
      emptyList(),
      0,
      0.0,
      0,
      "",
      name
   )
}
