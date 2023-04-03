package si.inova.androidarchitectureplayground.products.data

import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import si.inova.androidarchitectureplayground.products.data.model.ProductListDto
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.retrofit.SyntheticHeaders

interface ProductsService {
   @GET("/products")
   fun getProducts(
      @Header(SyntheticHeaders.HEADER_FORCE_REFRESH)
      force: Boolean = false,
      @Query("skip")
      skip: Int = 0
   ): Flow<Outcome<ProductListDto>>
}
