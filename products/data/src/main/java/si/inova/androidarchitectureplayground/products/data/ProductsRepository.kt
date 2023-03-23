package si.inova.androidarchitectureplayground.products.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import si.inova.androidarchitectureplayground.common.outcome.Outcome
import si.inova.androidarchitectureplayground.common.outcome.mapData
import si.inova.androidarchitectureplayground.network.cache.DiskCache
import si.inova.androidarchitectureplayground.products.data.model.ProductDto
import javax.inject.Inject

class ProductsRepository @Inject constructor(
   private val productsService: ProductsService,
   private val cache: DiskCache
) {
   fun getProducts(force: Boolean): Flow<Outcome<List<ProductDto>>> {

      return productsService.getProducts()
         .onStart {
            if (force) {
               cache.clearForRequest("/products")
            }
         }
         .map { outcome -> outcome.mapData { it.products } }
   }
}
