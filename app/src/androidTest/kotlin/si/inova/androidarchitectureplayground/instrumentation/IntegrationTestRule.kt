package si.inova.androidarchitectureplayground.instrumentation

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import okhttp3.mockwebserver.MockWebServer
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runners.model.MultipleFailureException
import org.junit.runners.model.Statement
import si.inova.androidarchitectureplayground.MainActivity
import si.inova.androidarchitectureplayground.util.registerStandardIdlingResources
import si.inova.kotlinova.core.logging.LogPriority
import si.inova.kotlinova.core.logging.logcat
import si.inova.kotlinova.retrofit.MockWebServerScope
import si.inova.kotlinova.retrofit.setJsonBody
import java.net.HttpURLConnection

class IntegrationTestRule(
   val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity> = createAndroidComposeRule(),
) : TestRule, ComposeTestRule by composeTestRule {
   val server = MockWebServer()

   var failTestForUnhandledExceptions: Boolean = true

   override fun apply(base: Statement?, description: Description?): Statement {
      return RuleChain.outerRule(testEvents).around(composeTestRule).apply(base, description)
   }

   init {
      TestNetworkUrlComponent.url = server.url("").toString()
   }

   inline fun runWithServer(
      block: MockWebServerScope.() -> Unit,
   ) {
      val scope = MockWebServerScope(server, server.url("").toString())
      server.dispatcher = scope

      scope.setDefaultResponse { request ->
         val url = request.path ?: "UNKNOWN URL"
         logcat("Tests", LogPriority.ERROR) { "Response to $url not mocked" }

         setResponseCode(HttpURLConnection.HTTP_INTERNAL_ERROR)
         setJsonBody("{\"message\":\"Response to $url not mocked\"}")
      }

      try {
         block(scope)
      } finally {
         server.shutdown()
      }
   }

   private val testEvents = object : TestWatcher() {
      override fun starting(description: Description?) {
         TestErrorReportingComponent.caughtExceptions.clear()

         composeTestRule.registerStandardIdlingResources()
      }

      override fun finished(description: Description?) {
         if (failTestForUnhandledExceptions) {
            MultipleFailureException.assertEmpty(TestErrorReportingComponent.caughtExceptions)
         }
      }
   }
}
