package si.inova.androidarchitectureplayground.products.ui

import androidx.compose.runtime.Stable
import com.androidarchitectureplayground.products.data.model.ProductDto
import kotlinx.coroutines.flow.StateFlow
import si.inova.androidarchitectureplayground.common.outcome.Outcome
import si.inova.androidarchitectureplayground.navigation.base.ScopedService

@Stable
interface ProductListViewModel : ScopedService {
   val products: StateFlow<Outcome<List<ProductDto>>>
   fun refresh()
}
