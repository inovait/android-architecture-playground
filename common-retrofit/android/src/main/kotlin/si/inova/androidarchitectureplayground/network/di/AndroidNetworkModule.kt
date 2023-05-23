package si.inova.androidarchitectureplayground.network.di

import android.content.Context
import com.appmattus.certificatetransparency.cache.AndroidDiskCache
import com.appmattus.certificatetransparency.cache.DiskCache
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import dagger.Provides
import si.inova.androidarchitectureplayground.common.di.ApplicationScope
import si.inova.androidarchitectureplayground.network.services.AndroidServiceFactory
import si.inova.androidarchitectureplayground.network.services.ServiceFactory
import si.inova.kotlinova.core.reporting.ErrorReporter
import si.inova.kotlinova.retrofit.caching.GlobalOkHttpDiskCacheManager
import javax.inject.Singleton

@Module
@ContributesTo(ApplicationScope::class)
abstract class AndroidNetworkModule {
   @Binds
   abstract fun AndroidServiceFactory.bindToServiceFactory(): ServiceFactory

   companion object {
      @Provides
      fun provideDiskCacheManager(
         context: Context,
         errorReporter: ErrorReporter,
      ): GlobalOkHttpDiskCacheManager {
         return GlobalOkHttpDiskCacheManager(context, errorReporter)
      }

      @Provides
      @Singleton
      fun provideCertificateTransparencyDiskCache(context: Context): DiskCache {
         return AndroidDiskCache(context)
      }
   }
}
