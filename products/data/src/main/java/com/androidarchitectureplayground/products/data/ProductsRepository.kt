package com.androidarchitectureplayground.products.data

import com.androidarchitectureplayground.products.data.model.ProductDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import si.inova.androidarchitectureplayground.common.outcome.Outcome
import javax.inject.Inject

class ProductsRepository @Inject constructor(private val productsService: ProductsService) {
   suspend fun getProducts(): Flow<Outcome<List<ProductDto>>> {
      return flowOf(Outcome.Success(productsService.getProducts().products))
   }
}
