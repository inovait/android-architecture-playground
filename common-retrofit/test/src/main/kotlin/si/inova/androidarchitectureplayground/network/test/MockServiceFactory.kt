package si.inova.androidarchitectureplayground.network.test

import kotlinx.coroutines.test.TestScope
import si.inova.androidarchitectureplayground.network.di.NetworkModule
import si.inova.androidarchitectureplayground.network.services.BaseServiceFactory
import si.inova.kotlinova.core.test.outcomes.ThrowingErrorReporter
import si.inova.kotlinova.retrofit.MockWebServerScope

fun MockWebServerScope.serviceFactory(testScope: TestScope): BaseServiceFactory {
   return BaseServiceFactory(
      testScope,
      { NetworkModule.provideMoshi(emptySet()) },
      { NetworkModule.prepareDefaultOkHttpClient().build() },
      ThrowingErrorReporter(testScope),
      baseUrl
   )
}
