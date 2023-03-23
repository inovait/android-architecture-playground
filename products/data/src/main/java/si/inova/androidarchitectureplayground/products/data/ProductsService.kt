package si.inova.androidarchitectureplayground.products.data

import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import si.inova.androidarchitectureplayground.common.outcome.Outcome
import si.inova.androidarchitectureplayground.products.data.model.ProductListDto

interface ProductsService {
   @GET("/products")
   fun getProducts(): Flow<Outcome<ProductListDto>>
}
