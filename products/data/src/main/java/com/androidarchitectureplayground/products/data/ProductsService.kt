package com.androidarchitectureplayground.products.data

import com.androidarchitectureplayground.products.data.model.ProductListDto
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import si.inova.androidarchitectureplayground.common.outcome.Outcome

interface ProductsService {
   @GET("/products")
   fun getProducts(): Flow<Outcome<ProductListDto>>
}
