package si.inova.androidarchitectureplayground.products.ui

import com.androidarchitectureplayground.products.data.model.ProductDto
import kotlinx.coroutines.flow.MutableStateFlow
import si.inova.androidarchitectureplayground.common.outcome.Outcome

class FakeProductListViewModel : ProductListViewModel {
   override val products = MutableStateFlow<Outcome<List<ProductDto>>>(Outcome.Progress())
   var refreshCalled: Boolean = false

   override fun refresh() {
      refreshCalled = true
   }
}
