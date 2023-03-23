package si.inova.androidarchitectureplayground.products.data

import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Header
import si.inova.androidarchitectureplayground.common.outcome.Outcome
import si.inova.androidarchitectureplayground.network.services.HEADER_FORCE_REFRESH
import si.inova.androidarchitectureplayground.products.data.model.ProductListDto

interface ProductsService {
   @GET("/products")
   fun getProducts(@Header(HEADER_FORCE_REFRESH) force: Boolean): Flow<Outcome<ProductListDto>>
}
