package si.inova.androidarchitectureplayground.products.data

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import si.inova.androidarchitectureplayground.common.PureApplicationScope
import si.inova.androidarchitectureplayground.network.services.ServiceFactory
import si.inova.androidarchitectureplayground.network.services.create
import si.inova.androidarchitectureplayground.network.services.okHttp
import javax.inject.Singleton

@Module
@ContributesTo(PureApplicationScope::class)
class ProductsDataModule {
   @Provides
   @Singleton
   fun provideProductService(serviceFactory: ServiceFactory): ProductsService = serviceFactory.create {
      okHttp {
         addNetworkInterceptor {
            @Suppress("MagicNumber") // Demo purposes
            Thread.sleep(1_000) // Add extra delay to demonstrate cache
            val response = it.proceed(it.request())

            // Dummyjson response contains no-cache by default. Remove to demonstrate cache use.
            response.newBuilder().removeHeader("Cache-Control").build()
         }
      }
   }
}
