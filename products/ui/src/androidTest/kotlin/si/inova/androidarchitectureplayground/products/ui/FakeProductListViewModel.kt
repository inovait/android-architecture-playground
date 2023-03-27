package si.inova.androidarchitectureplayground.products.ui

import kotlinx.coroutines.flow.MutableStateFlow
import si.inova.androidarchitectureplayground.common.outcome.Outcome

class FakeProductListViewModel : ProductListViewModel {
   override val products: MutableStateFlow<Outcome<ProductListScreenData>> = MutableStateFlow(Outcome.Progress())
   var refreshCalled: Boolean = false
   var nextPageCalled: Boolean = false

   override fun refresh() {
      refreshCalled = true
   }

   override fun nextPage() {
      nextPageCalled = true
   }
}
