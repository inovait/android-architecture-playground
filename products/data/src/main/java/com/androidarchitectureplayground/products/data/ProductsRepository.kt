package com.androidarchitectureplayground.products.data

import com.androidarchitectureplayground.products.data.model.ProductDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import si.inova.androidarchitectureplayground.common.outcome.Outcome
import si.inova.androidarchitectureplayground.common.outcome.mapData
import javax.inject.Inject

class ProductsRepository @Inject constructor(private val productsService: ProductsService) {
   fun getProducts(): Flow<Outcome<List<ProductDto>>> {
      return productsService.getProducts().map { outcome -> outcome.mapData { it.products } }
   }
}
