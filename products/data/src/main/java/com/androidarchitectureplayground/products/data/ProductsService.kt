package com.androidarchitectureplayground.products.data

import com.androidarchitectureplayground.products.data.model.ProductListDto
import retrofit2.http.GET

interface ProductsService {
   @GET("/products")
   suspend fun getProducts(): ProductListDto
}
