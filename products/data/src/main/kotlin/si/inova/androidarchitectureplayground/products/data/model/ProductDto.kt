package si.inova.androidarchitectureplayground.products.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProductListDto(
   val products: List<ProductDto>
)

@JsonClass(generateAdapter = true)
data class ProductDto(
   val brand: String,
   val category: String,
   val description: String,
   val discountPercentage: Double,
   val id: Int,
   val images: List<String>,
   val price: Int,
   val rating: Double,
   val stock: Int,
   val thumbnail: String,
   val title: String
)
