package si.inova.androidarchitectureplayground.products.data

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import si.inova.androidarchitectureplayground.common.outcome.Outcome
import si.inova.androidarchitectureplayground.network.test.mockWebServer
import si.inova.androidarchitectureplayground.network.test.setJsonBodyFromResource
import si.inova.androidarchitectureplayground.products.data.model.ProductDto
import si.inova.androidarchitectureplayground.products.data.model.ProductListDto

class ProductsServiceTest {
   @Test
   internal fun `List products`() = runTest {
      mockWebServer {
         val service = ProductsDataModule().provideProductService(serviceFactory)

         mockResponse("/products") {
            setJsonBodyFromResource("product_list.json")
         }

         val expectedProducts = ProductListDto(
            listOf(
               ProductDto(
                  id = 1,
                  title = "iPhone 9",
                  description = "An apple mobile which is nothing like apple",
                  price = 549,
                  discountPercentage = 12.96,
                  rating = 4.69,
                  stock = 94,
                  brand = "Apple",
                  category = "smartphones",
                  thumbnail = "https://i.dummyjson.com/data/products/1/thumbnail.jpg",
                  images = listOf(
                     "https://i.dummyjson.com/data/products/1/1.jpg",
                     "https://i.dummyjson.com/data/products/1/2.jpg",
                     "https://i.dummyjson.com/data/products/1/3.jpg",
                     "https://i.dummyjson.com/data/products/1/4.jpg",
                     "https://i.dummyjson.com/data/products/1/thumbnail.jpg"
                  )
               ),
               ProductDto(
                  id = 2,
                  title = "iPhone X",
                  description = "SIM-Free, Model A19211 6.5-inch " +
                     "Super Retina HD display with OLED technology A12 Bionic chip with ...",
                  price = 899,
                  discountPercentage = 17.94,
                  rating = 4.44,
                  stock = 34,
                  brand = "Apple",
                  category = "smartphones",
                  thumbnail = "https://i.dummyjson.com/data/products/2/thumbnail.jpg",
                  images = listOf(
                     "https://i.dummyjson.com/data/products/2/1.jpg",
                     "https://i.dummyjson.com/data/products/2/2.jpg",
                     "https://i.dummyjson.com/data/products/2/3.jpg",
                     "https://i.dummyjson.com/data/products/2/thumbnail.jpg"
                  )
               )
            )
         )

         val products = service.getProducts().filterIsInstance<Outcome.Success<ProductListDto>>().first()

         products.data shouldBe expectedProducts
      }
   }
}
