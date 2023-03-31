package si.inova.androidarchitectureplayground.network.di

import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import si.inova.androidarchitectureplayground.network.android.util.GlobalOkHttpDiskCacheManager
import si.inova.androidarchitectureplayground.network.cache.DiskCache
import si.inova.androidarchitectureplayground.network.services.AndroidServiceFactory
import si.inova.androidarchitectureplayground.network.services.ServiceFactory
import si.inova.kotlinova.core.di.PureApplicationScope

@Module
@ContributesTo(PureApplicationScope::class)
abstract class AndroidNetworkModule {
   @Binds
   abstract fun AndroidServiceFactory.bindToServiceFactory(): ServiceFactory

   @Binds
   abstract fun GlobalOkHttpDiskCacheManager.bindToDiskCache(): DiskCache
}
