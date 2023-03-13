package com.androidarchitectureplayground.network.di

import com.androidarchitectureplayground.network.android.util.GlobalOkHttpDiskCacheManager
import com.androidarchitectureplayground.network.cache.DiskCache
import com.androidarchitectureplayground.network.services.AndroidServiceFactory
import com.androidarchitectureplayground.network.services.ServiceFactory
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import si.inova.androidarchitectureplayground.common.PureApplicationScope

@Module
@ContributesTo(PureApplicationScope::class)
abstract class AndroidNetworkModule {
   @Binds
   abstract fun AndroidServiceFactory.bindToServiceFactory(): ServiceFactory

   @Binds
   abstract fun GlobalOkHttpDiskCacheManager.bindToDiskCache(): DiskCache
}
