package si.inova.androidarchitectureplayground.products.ui

import androidx.compose.runtime.Stable
import com.androidarchitectureplayground.products.data.ProductsRepository
import com.androidarchitectureplayground.products.data.model.ProductDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import si.inova.androidarchitectureplayground.common.outcome.CoroutineResourceManager
import si.inova.androidarchitectureplayground.common.outcome.Outcome
import si.inova.androidarchitectureplayground.navigation.base.SingleScreenViewModel
import si.inova.androidarchitectureplayground.screens.ProductListScreenKey
import javax.inject.Inject

@Stable
class ProductListViewModel @Inject constructor(
   private val resources: CoroutineResourceManager,
   private val productsRepository: ProductsRepository
) : SingleScreenViewModel<ProductListScreenKey>(resources.scope) {
   private val _products = MutableStateFlow<Outcome<List<ProductDto>>>(Outcome.Progress())
   val products: StateFlow<Outcome<List<ProductDto>>> = _products

   override fun onServiceRegistered() {
      loadProducts()
   }

   private fun loadProducts() {
      resources.launchResourceControlTask(_products) {
         emitAll(productsRepository.getProducts())
      }
   }
}
