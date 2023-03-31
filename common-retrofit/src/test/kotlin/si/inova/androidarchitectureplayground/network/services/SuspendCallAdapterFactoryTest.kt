package si.inova.androidarchitectureplayground.network.services

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.test.runTest
import okhttp3.Cache
import okhttp3.mockwebserver.SocketPolicy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import si.inova.androidarchitectureplayground.network.test.mockWebServer
import si.inova.androidarchitectureplayground.network.test.setJsonBody
import si.inova.kotlinova.core.exceptions.DataParsingException
import si.inova.kotlinova.core.exceptions.NoNetworkException
import si.inova.kotlinova.core.outcome.CauseException
import java.io.File
import java.util.concurrent.TimeUnit

class SuspendCallAdapterFactoryTest {
   private lateinit var tempCache: Cache

   @BeforeEach
   internal fun setUp(
      @TempDir
      cacheDirectory: File
   ) {
      tempCache = Cache(cacheDirectory, 100_000)
   }

   @Test
   internal fun `Throw NoNetworkException if request timeouts`() = runTest {
      mockWebServer {
         val service: TestRetrofitService = serviceFactory.create {
            okHttp {
               callTimeout(100, TimeUnit.MILLISECONDS)
            }
         }

         mockResponse("/data") {
            socketPolicy = SocketPolicy.NO_RESPONSE
         }

         shouldThrow<NoNetworkException> {
            service.getEnumResult()
         }
      }
   }

   @Test
   internal fun `Throw NoNetworkException if request fails to download`() = runTest {
      mockWebServer {
         val service: TestRetrofitService = serviceFactory.create()

         mockResponse("/data") {
            socketPolicy = SocketPolicy.DISCONNECT_DURING_RESPONSE_BODY
         }

         shouldThrow<NoNetworkException> {
            service.getEnumResult()
         }
      }
   }

   @Test
   internal fun `Throw DataParsingException if json parsing fails`() = runTest {
      mockWebServer {
         val service: TestRetrofitService = serviceFactory.create()

         mockResponse("/data") {
            setJsonBody("{")
         }

         shouldThrow<DataParsingException> {
            service.getEnumResult()
         }
      }
   }

   @Test
   internal fun `Throw DataParsingException if json parsing has wrong fields`() = runTest {
      mockWebServer {
         val service: TestRetrofitService = serviceFactory.create()

         mockResponse("/data") {
            setJsonBody("\"THIRD\"")
         }

         shouldThrow<DataParsingException> {
            service.getEnumResult()
         }
      }
   }

   @Test
   internal fun `Include URL in the json parsing exceptions`() = runTest {
      mockWebServer {
         val service: TestRetrofitService = serviceFactory.create()

         mockResponse("/data") {
            setJsonBody("\"THIRD\"")
         }

         val exception = shouldThrow<DataParsingException> {
            service.getEnumResult()
         }

         exception.message.shouldNotBeNull().apply {
            shouldContain(" http://localhost")
            shouldContain("/data")
         }
      }
   }

   @Test
   internal fun `Parse error with error handler`() = runTest {
      mockWebServer {
         val service: TestRetrofitService = serviceFactory.create {
            errorHandler = ErrorHandler { response: Response<*>, cause: Exception ->
               val text = response.errorBody()?.string() ?: "NO_ERROR_MESSAGE"
               TestErrorResponseException(text, cause)
            }
         }

         mockResponse("/data") {
            setStatus("HTTP/1.1 404 NOT FOUND")
            setJsonBody("\"TEST ERROR MESSAGE\"")
         }

         val exception = shouldThrow<TestErrorResponseException> {
            service.getEnumResult()
         }

         exception.cause.shouldNotBeNull().message.apply {
            shouldContain(" http://localhost")
            shouldContain("/data")
         }
      }
   }

   @Test
   internal fun `Return cached version if cached version is still fresh`() = runTest {
      mockWebServer {
         val service: TestRetrofitService = serviceFactory.create {
            okHttp {
               cache(tempCache)
            }
         }

         mockResponse("/data") {
            setHeader("ETag", "a")
            setHeader("Cache-Control", "max-age=100000")
            setJsonBody("\"FIRST\"")
         }

         service.getEnumResult()

         service.getEnumResult() shouldBe FakeEnumResult.FIRST

         server.requestCount shouldBe 1
      }
   }

   @Test
   internal fun `Ignore cache freshness when synthetic force refresh header is set`() = runTest {
      mockWebServer {
         val service: TestRetrofitService = serviceFactory.create {
            okHttp {
               cache(tempCache)
            }
         }

         mockResponse("/data") {
            setHeader("ETag", "a")
            setHeader("Cache-Control", "max-age=100000")
            setJsonBody("\"FIRST\"")
         }

         service.getEnumResult()

         mockResponse("/data") {
            setJsonBody("\"SECOND\"")
         }

         service.getEnumResult(force = true) shouldBe FakeEnumResult.SECOND
      }
   }

   @Test
   internal fun `Remove synthetic force refresh header when set`() = runTest {
      mockWebServer {
         val service: TestRetrofitService = serviceFactory.create {
            okHttp {
               cache(tempCache)
            }
         }

         mockResponse("/data") {
            setHeader("ETag", "a")
            setHeader("Cache-Control", "max-age=100000")
            setJsonBody("\"FIRST\"")
         }

         service.getEnumResult()

         mockResponse("/data") {
            setJsonBody("\"SECOND\"")
         }

         service.getEnumResult(force = true) shouldBe FakeEnumResult.SECOND

         server.takeRequest().getHeader(HEADER_FORCE_REFRESH).shouldBeNull()
         server.takeRequest().getHeader(HEADER_FORCE_REFRESH).shouldBeNull()
      }
   }

   private interface TestRetrofitService {
      @GET("/data")
      suspend fun getEnumResult(
         @Header(HEADER_FORCE_REFRESH)
         force: Boolean = false
      ): FakeEnumResult
   }

   private enum class FakeEnumResult {
      FIRST,
      SECOND
   }

   private class TestErrorResponseException(message: String? = null, cause: Throwable? = null) : CauseException(message, cause)
}
