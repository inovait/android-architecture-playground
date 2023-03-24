package si.inova.androidarchitectureplayground.network.services

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.jupiter.api.Test
import retrofit2.http.GET
import si.inova.androidarchitectureplayground.network.test.mockWebServer
import si.inova.androidarchitectureplayground.network.test.setJsonBody

class BaseServiceFactoryTest {
   @Test
   internal fun `Create basic service that returns data`() = runTest {
      mockWebServer {
         val service: TestRetrofitService = serviceFactory.create()

         mockResponse("/data") {
            setJsonBody("\"Hello\"")
         }

         service.getResult() shouldBe "Hello"
      }
   }

   @Test
   internal fun `Use modified okHttp client when okHttp clause is used`() = runTest {
      mockWebServer {
         val service: TestRetrofitService = serviceFactory.create {
            okHttp {
               addNetworkInterceptor {
                  it.proceed(it.request()).newBuilder().body("\"World\"".toResponseBody()).build()
               }
            }
         }

         mockResponse("/data") {
            setJsonBody("\"Hello\"")
         }

         service.getResult() shouldBe "World"
      }
   }

   interface TestRetrofitService {
      @GET("/data")
      suspend fun getResult(): String
   }
}
