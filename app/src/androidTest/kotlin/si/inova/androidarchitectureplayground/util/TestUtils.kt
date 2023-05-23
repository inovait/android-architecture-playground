package si.inova.androidarchitectureplayground.util

import androidx.compose.ui.test.junit4.ComposeTestRule
import si.inova.androidarchitectureplayground.instrumentation.TestCoroutinesModule
import si.inova.kotlinova.compose.androidtest.idlingresource.LoadingCountingIdlingResource
import si.inova.kotlinova.compose.androidtest.idlingresource.registerIdlingDispatchers

fun ComposeTestRule.registerStandardIdlingResources() {
   registerIdlingDispatchers(TestCoroutinesModule.idlingDispatcherProvider)
   registerIdlingResource(LoadingCountingIdlingResource)
}
