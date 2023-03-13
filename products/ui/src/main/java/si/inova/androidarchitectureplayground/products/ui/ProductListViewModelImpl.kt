package si.inova.androidarchitectureplayground.products.ui

import androidx.compose.runtime.Stable
import com.androidarchitectureplayground.products.data.ProductsRepository
import com.androidarchitectureplayground.products.data.model.ProductDto
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.MutableStateFlow
import si.inova.androidarchitectureplayground.common.outcome.CoroutineResourceManager
import si.inova.androidarchitectureplayground.common.outcome.Outcome
import si.inova.androidarchitectureplayground.navigation.base.SingleScreenViewModel
import si.inova.androidarchitectureplayground.screens.ProductListScreenKey
import javax.inject.Inject

@Stable
@ContributesBinding(ApplicationScope::class, ProductListViewModel::class)
class ProductListViewModelImpl @Inject constructor(
   private val resources: CoroutineResourceManager,
   private val productsRepository: ProductsRepository
) : SingleScreenViewModel<ProductListScreenKey>(resources.scope), ProductListViewModel {
   override val products = MutableStateFlow<Outcome<List<ProductDto>>>(Outcome.Progress())

   override fun onServiceRegistered() {
      loadProducts(force = false)
   }

   private fun loadProducts(force: Boolean) {
      resources.launchResourceControlTask(products) {
         emitAll(productsRepository.getProducts(force))
      }
   }

   override fun refresh() {
      loadProducts(true)
   }
}
