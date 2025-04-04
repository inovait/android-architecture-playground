package si.inova.androidarchitectureplayground.instrumentation

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import org.junit.runners.model.MultipleFailureException
import org.junit.runners.model.Statement
import si.inova.androidarchitectureplayground.MainActivity
import si.inova.kotlinova.retrofit.MockWebServerScope

class IntegrationTestRule(
   val composeTestRule: AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity> = createAndroidComposeRule(),
) : TestRule, ComposeTestRule by composeTestRule {
   val scope = MockWebServerScope()

   var failTestForUnhandledExceptions: Boolean = true

   override fun apply(base: Statement?, description: Description?): Statement {
      return RuleChain.outerRule(testEvents).around(composeTestRule).apply(base, description)
   }

   init {
      TestNetworkUrlComponent.url = scope.server.url("").toString()
   }

   private val testEvents = object : TestWatcher() {
      override fun starting(description: Description?) {
         TestErrorReportingComponent.caughtExceptions.clear()
      }

      override fun finished(description: Description?) {
         if (failTestForUnhandledExceptions) {
            MultipleFailureException.assertEmpty(TestErrorReportingComponent.caughtExceptions)
         }
      }
   }
}
