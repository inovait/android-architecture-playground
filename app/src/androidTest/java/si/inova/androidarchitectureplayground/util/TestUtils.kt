package si.inova.androidarchitectureplayground.util

import androidx.compose.ui.test.IdlingResource
import androidx.compose.ui.test.MainTestClock
import androidx.compose.ui.test.junit4.ComposeTestRule
import dispatch.core.DefaultCoroutineScope
import okhttp3.mockwebserver.MockWebServer
import si.inova.androidarchitectureplayground.common.time.TimeProvider
import si.inova.androidarchitectureplayground.instrumentation.TestCoroutinesModule
import si.inova.androidarchitectureplayground.instrumentation.TestNetworkUrlModule
import si.inova.androidarchitectureplayground.network.di.NetworkModule
import si.inova.androidarchitectureplayground.network.services.BaseServiceFactory
import si.inova.androidarchitectureplayground.network.test.MockWebServerScope
import si.inova.androidarchitectureplayground.test.time.FakeTimeProvider
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Create and prepare [MockWebServer] for creating tests for web services.
 *
 * You can use [MockWebServerScope.serviceFactory] inside the provided block to initialize your Retrofit service
 * and [MockWebServerScope.mockResponse] to create mock HTTP responses.
 */
inline fun ComposeTestRule.mockWebServer(block: MockWebServerScope.() -> Unit) {
   val server = MockWebServer()

   val networkModule = NetworkModule()
   val baseUrl = server.url("").toString()
   val serviceFactory = BaseServiceFactory(
      DefaultCoroutineScope(dispatcherProvider = TestCoroutinesModule.dispatcherProvider),
      { networkModule.provideMoshi() },
      { networkModule.provideOkHttpClient() },
      { throw it },
      mainClock.virtualTimeProvider(),
      baseUrl
   )

   TestNetworkUrlModule.url = baseUrl

   val scope = MockWebServerScope(server, serviceFactory)
   server.dispatcher = scope

   try {
      block(scope)
   } finally {
      server.shutdown()
   }
}

/**
 * A [TimeProvider] instance that provides time based on virtual clock of this kotlin test.
 */
fun MainTestClock.virtualTimeProvider(
   currentLocalDate: () -> LocalDate = { LocalDate.MIN },
   currentLocalTime: () -> LocalTime = { LocalTime.MIN },
   currentLocalDateTime: () -> LocalDateTime = { LocalDateTime.of(currentLocalDate(), currentLocalTime()) },
   currentTimezone: () -> ZoneId = { ZoneId.of("UTC") },
   currentZonedDateTime: (() -> ZonedDateTime)? = null
): TimeProvider {
   return FakeTimeProvider(
      currentLocalDate,
      currentLocalTime,
      currentLocalDateTime,
      currentTimezone,
      currentZonedDateTime = currentZonedDateTime ?: {
         ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(currentTime),
            currentTimezone()
         )
      },
   ) { currentTime }
}

fun ComposeTestRule.registerStandardIdlingResources() {
   with(TestCoroutinesModule.dispatcherProvider) {
      listOf(
         default,
         io,
         main,
         mainImmediate,
         unconfined
      ).forEach {
         it as IdlingDispatcher

         val composeIdlingResource = object : IdlingResource {
            override val isIdleNow: Boolean
               get() = it.isIdle()

            override fun getDiagnosticMessageIfBusy(): String {
               return "Dispatcher ${it.delegate} is busy"
            }
         }

         registerIdlingResource(composeIdlingResource)
      }
   }

   registerIdlingResource(LoadingCountingIdlingResource)
}
