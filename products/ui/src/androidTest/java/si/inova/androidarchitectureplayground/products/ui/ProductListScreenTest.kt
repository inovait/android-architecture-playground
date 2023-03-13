package si.inova.androidarchitectureplayground.products.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.androidarchitectureplayground.products.data.model.ProductDto
import io.kotest.matchers.shouldBe
import org.junit.Rule
import org.junit.Test
import si.inova.androidarchitectureplayground.common.outcome.Outcome
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
         listOf(
            fakeProduct("A"),
            fakeProduct("B"),
            fakeProduct("C")
         )
      )

      composeTestRule.onNodeWithText("Products: A\nB\nC").assertIsDisplayed()
   }

   @Test
   fun showListOfProductsWhileLoading() {
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

      composeTestRule.onNodeWithText("Products: A\nB\nC").assertIsDisplayed()
   }

   @Test
   fun showLoaderWhileLoading() {
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

      composeTestRule.onNodeWithTag("loader").assertIsDisplayed()
   }

   @Test
   fun hideLoaderOnSuccess() {
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

      composeTestRule.mainClock.advanceTimeBy(600)
      composeTestRule.onNodeWithTag("loader").assertDoesNotExist()
   }

   @Test
   fun refresh() {
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
