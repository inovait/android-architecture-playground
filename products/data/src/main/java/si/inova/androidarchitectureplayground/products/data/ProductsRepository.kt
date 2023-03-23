package si.inova.androidarchitectureplayground.products.data

import kotlinx.coroutines.flow.map
import si.inova.androidarchitectureplayground.common.outcome.mapData
import si.inova.androidarchitectureplayground.common.pagination.OffsetBasedPaginatedDataStream
import si.inova.androidarchitectureplayground.common.pagination.PaginatedDataStream
import si.inova.androidarchitectureplayground.products.data.model.ProductDto
import javax.inject.Inject

class ProductsRepository @Inject constructor(
   private val productsService: ProductsService
) {
   fun getProducts(force: Boolean): PaginatedDataStream<List<ProductDto>> {

      return OffsetBasedPaginatedDataStream { offset ->
         productsService.getProducts(force, offset).map { outcome -> outcome.mapData { it.products } }
      }
   }
}

