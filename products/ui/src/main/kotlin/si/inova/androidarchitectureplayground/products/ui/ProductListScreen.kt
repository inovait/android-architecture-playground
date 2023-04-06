package si.inova.androidarchitectureplayground.products.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import si.inova.androidarchitectureplayground.screens.ProductListScreenKey
import si.inova.androidarchitectureplayground.ui.errors.commonUserFriendlyMessage
import si.inova.kotlinova.compose.flow.collectAsStateWithLifecycleAndBlinkingPrevention
import si.inova.kotlinova.core.outcome.LoadingStyle
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.screens.Screen

class ProductListScreen(private val viewModel: ProductListViewModel) : Screen<ProductListScreenKey>() {
   @Composable
   override fun Content(key: ProductListScreenKey) {
      val screenState = viewModel.products.collectAsStateWithLifecycleAndBlinkingPrevention().value
      val productList = screenState?.data?.items ?: emptyList()

      Column {
         Box(Modifier.size(48.dp), propagateMinConstraints = true) {
            if (screenState is Outcome.Progress && screenState.style != LoadingStyle.ADDITIONAL_DATA) {
               CircularProgressIndicator(Modifier.testTag("loader"))
            }
         }

         Button(onClick = viewModel::refresh) {
            Text("Refresh")
         }
         Text("Products:")
         if (screenState is Outcome.Error) {
            Text("ERROR: '${screenState.exception.commonUserFriendlyMessage()}'")
         }

         val state = rememberLazyListState()
         state.DetectBottomScroll(viewModel::nextPage)

         LazyColumn(state = state) {
            items(productList) {
               Text(it.title)
            }

            if (screenState?.data?.anyLeft == true) {
               item {
                  if (screenState is Outcome.Progress && screenState.style == LoadingStyle.ADDITIONAL_DATA) {
                     CircularProgressIndicator()
                  } else {
                     Box(Modifier.size(40.dp))
                  }
               }
            }
         }
      }
   }

   @Composable
   private fun LazyListState.DetectBottomScroll(callback: () -> Unit) {
      LaunchedEffect(this, callback) {
         snapshotFlow {
            layoutInfo.visibleItemsInfo.lastOrNull()?.index == (layoutInfo.totalItemsCount - 1)
         }.collect { atBottom ->
            if (atBottom) {
               callback()
            }
         }
      }
   }
}
