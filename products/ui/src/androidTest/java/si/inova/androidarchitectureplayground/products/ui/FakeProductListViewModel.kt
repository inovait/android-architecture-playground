package si.inova.androidarchitectureplayground.products.ui

import kotlinx.coroutines.flow.MutableStateFlow
import si.inova.androidarchitectureplayground.common.outcome.Outcome
import si.inova.androidarchitectureplayground.products.data.model.ProductDto

class FakeProductListViewModel : ProductListViewModel {
   override val products = MutableStateFlow<Outcome<List<ProductDto>>>(Outcome.Progress())
   var refreshCalled: Boolean = false

   override fun refresh() {
      refreshCalled = true
   }
}
