package si.inova.androidarchitectureplayground.products.ui

import androidx.compose.runtime.Stable
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import si.inova.androidarchitectureplayground.common.pagination.PaginatedDataStream
import si.inova.androidarchitectureplayground.navigation.base.SingleScreenViewModel
import si.inova.androidarchitectureplayground.products.data.ProductsRepository
import si.inova.androidarchitectureplayground.screens.ProductListScreenKey
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.core.outcome.mapData
import javax.inject.Inject

@Stable
@ContributesBinding(ApplicationScope::class, ProductListViewModel::class)
class ProductListViewModelImpl @Inject constructor(
   private val resources: CoroutineResourceManager,
   private val productsRepository: ProductsRepository
) : SingleScreenViewModel<ProductListScreenKey>(resources.scope), ProductListViewModel {
   override val products = MutableStateFlow<Outcome<ProductListScreenData>>(Outcome.Progress())
   private var productsPaginator: PaginatedDataStream<*>? = null

   override fun onServiceRegistered() {
      loadProducts(force = false)
   }

   private fun loadProducts(force: Boolean) {
      resources.launchResourceControlTask(products) {
         val paginator = productsRepository.getProducts(force)
         productsPaginator = paginator
         emitAll(paginator.data.map { paginationResult ->
            paginationResult.items.mapData {
               ProductListScreenData(it, paginationResult.hasAnyDataLeft)
            }
         })
      }
   }

   override fun refresh() {
      loadProducts(true)
   }

   override fun nextPage() {
      productsPaginator?.nextPage()
   }
}
