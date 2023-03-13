package si.inova.androidarchitectureplayground.products.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import si.inova.androidarchitectureplayground.common.outcome.Outcome
import si.inova.androidarchitectureplayground.common.outcome.valueOrNull
import si.inova.androidarchitectureplayground.navigation.base.Screen
import si.inova.androidarchitectureplayground.screens.ProductListScreenKey
import si.inova.androidarchitectureplayground.ui.errors.commonUserFriendlyMessage
import si.inova.androidarchitectureplayground.ui.time.collectAsStateWithLifecycleAndBlinkingPrevention

class ProductListScreen(private val viewModel: ProductListViewModel) : Screen<ProductListScreenKey>() {
   @Composable
   override fun Content(key: ProductListScreenKey) {
      val productList = viewModel.products.collectAsStateWithLifecycleAndBlinkingPrevention().value

      Column(Modifier.verticalScroll(rememberScrollState())) {
         CircularProgressIndicator(Modifier.alpha(if (productList is Outcome.Progress) 1f else 0f))

         Button(onClick = viewModel::refresh) {
            Text("Refresh")
         }
         Text("Products: ${productList?.valueOrNull?.joinToString("\n") { it.title } ?: "NULL"}")
         if (productList is Outcome.Error) {
            Text("ERROR: '${productList.exception.commonUserFriendlyMessage()}'")
         }
      }
   }
}
