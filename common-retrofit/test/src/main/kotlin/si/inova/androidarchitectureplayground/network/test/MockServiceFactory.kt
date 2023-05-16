package si.inova.androidarchitectureplayground.network.test

import kotlinx.coroutines.test.TestScope
import si.inova.androidarchitectureplayground.network.di.NetworkModule
import si.inova.androidarchitectureplayground.network.exceptions.DefaultErrorHandler
import si.inova.androidarchitectureplayground.network.services.BaseServiceFactory
import si.inova.kotlinova.core.test.outcomes.ThrowingErrorReporter
import si.inova.kotlinova.retrofit.MockWebServerScope

fun MockWebServerScope.serviceFactory(testScope: TestScope): BaseServiceFactory {
   val moshi = NetworkModule.provideMoshi(emptySet())

   return BaseServiceFactory(
      testScope,
      { moshi },
      { NetworkModule.prepareDefaultOkHttpClient().build() },
      ThrowingErrorReporter(testScope),
      DefaultErrorHandler({ moshi }),
      baseUrl
   )
}
