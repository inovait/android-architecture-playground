package si.inova.androidarchitectureplayground.products.ui

import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.MutableStateFlow
import si.inova.androidarchitectureplayground.common.outcome.Outcome
import si.inova.androidarchitectureplayground.navigation.base.ScopedService
import si.inova.androidarchitectureplayground.products.data.model.ProductDto

@Stable
interface ProductListViewModel : ScopedService {
   val products: MutableStateFlow<Outcome<ProductListScreenData>>
   fun refresh()
   fun nextPage()
}

data class ProductListScreenData(
   val items: List<ProductDto>,
   val anyLeft: Boolean
)
