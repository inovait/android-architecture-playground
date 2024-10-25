package si.inova.androidarchitectureplayground.network.test

import kotlinx.coroutines.test.TestScope
import si.inova.androidarchitectureplayground.network.di.NetworkComponent
import si.inova.androidarchitectureplayground.network.exceptions.DefaultErrorHandler
import si.inova.androidarchitectureplayground.network.services.BaseServiceFactory
import si.inova.kotlinova.core.test.outcomes.ThrowingErrorReporter
import si.inova.kotlinova.retrofit.MockWebServerScope

fun MockWebServerScope.serviceFactory(testScope: TestScope): BaseServiceFactory {
   val networkComponent = object : NetworkComponent {}
   val moshi = networkComponent.provideMoshi(emptySet())

   return BaseServiceFactory(
      testScope,
      { moshi },
      { networkComponent.prepareDefaultOkHttpClient().build() },
      ThrowingErrorReporter(testScope),
      DefaultErrorHandler(),
      baseUrl
   )
}
