package com.androidarchitectureplayground.products.data

import com.androidarchitectureplayground.network.services.ServiceFactory
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import si.inova.androidarchitectureplayground.common.PureApplicationScope
import javax.inject.Singleton

@Module
@ContributesTo(PureApplicationScope::class)
class ProductsDataModule {
   @Provides
   @Singleton
   fun provideProductService(serviceFactory: ServiceFactory): ProductsService = serviceFactory.create()
}
