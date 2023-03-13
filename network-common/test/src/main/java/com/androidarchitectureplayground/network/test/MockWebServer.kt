package com.androidarchitectureplayground.network.test

import com.androidarchitectureplayground.network.di.NetworkModule
import com.androidarchitectureplayground.network.services.BaseServiceFactory
import kotlinx.coroutines.test.TestScope
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import org.intellij.lang.annotations.Language
import si.inova.androidarchitectureplayground.test.outcomes.throwingErrorReporter
import si.inova.androidarchitectureplayground.test.time.virtualTimeProvider

/**
 * Create and prepare [MockWebServer] for creating tests for web services.
 *
 * You can use [MockWebServerScope.serviceFactory] inside the provided block to initialize your Retrofit service
 * and [MockWebServerScope.mockResponse] to create mock HTTP responses.
 */
inline fun TestScope.mockWebServer(block: MockWebServerScope.() -> Unit) {
   val server = MockWebServer()

   val networkModule = NetworkModule()
   val serviceFactory = BaseServiceFactory(
      { networkModule.provideMoshi() },
      { networkModule.provideOkHttpClient() },
      throwingErrorReporter(),
      virtualTimeProvider(),
      server.url("").toString()
   )

   val scope = MockWebServerScope(server, serviceFactory)
   server.dispatcher = scope

   try {
      block(scope)
   } finally {
      server.shutdown()
   }
}

class MockWebServerScope(val server: MockWebServer, val serviceFactory: BaseServiceFactory) : Dispatcher() {
   private val responses = HashMap<String, MockResponse>()

   override fun dispatch(request: RecordedRequest): MockResponse {
      return responses[request.path] ?: error("Response to '${request.path ?: "null"}' not mocked")
   }

   fun mockResponse(url: String, response: MockResponse) {
      responses[url] = response
   }

   inline fun mockResponse(url: String, responseBuilder: MockResponse.() -> Unit) {
      val response = MockResponse()
      responseBuilder(response)

      mockResponse(url, response)
   }
}

fun MockResponse.setJsonBody(
   @Language("JSON")
   body: String
) {
   addHeader("Content-Type", "application/json")
   setBody(body)
}

fun MockResponse.setJsonBodyFromResource(fileName: String) {
   addHeader("Content-Type", "application/json")

   val resource = MockWebServerScope::class.java.classLoader.getResourceAsStream(fileName)
      ?: error("Resource $fileName does not exist")

   val body = resource.bufferedReader().use { it.readText() }
   setJsonBody(body)
}
