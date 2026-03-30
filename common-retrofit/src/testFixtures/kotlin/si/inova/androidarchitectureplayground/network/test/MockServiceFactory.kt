package si.inova.androidarchitectureplayground.network.test

import kotlinx.coroutines.test.TestScope
import si.inova.androidarchitectureplayground.network.di.NetworkProviders
import si.inova.androidarchitectureplayground.network.exceptions.DefaultErrorHandler
import si.inova.androidarchitectureplayground.network.services.BaseServiceFactory
import si.inova.kotlinova.core.test.outcomes.ThrowingErrorReporter
import si.inova.kotlinova.retrofit.MockWebServerScope

fun MockWebServerScope.serviceFactory(testScope: TestScope): BaseServiceFactory {
   val json = NetworkProviders.createJson(emptyMap())

   return BaseServiceFactory(
      testScope,
      { json },
      { NetworkProviders.prepareDefaultOkHttpClient().build() },
      ThrowingErrorReporter(testScope),
      DefaultErrorHandler(),
      baseUrl
   )
}
