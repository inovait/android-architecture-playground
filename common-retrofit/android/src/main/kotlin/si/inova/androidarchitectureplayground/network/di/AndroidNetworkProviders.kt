package si.inova.androidarchitectureplayground.network.di

import android.content.Context
import com.appmattus.certificatetransparency.cache.AndroidDiskCache
import com.appmattus.certificatetransparency.cache.DiskCache
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import si.inova.androidarchitectureplayground.network.services.AndroidServiceFactory
import si.inova.androidarchitectureplayground.network.services.ServiceFactory
import si.inova.kotlinova.core.reporting.ErrorReporter
import si.inova.kotlinova.retrofit.caching.GlobalOkHttpDiskCacheManager

@ContributesTo(AppScope::class)
interface AndroidNetworkProviders {
   @Provides
   fun bindToServiceFactory(androidServiceFactory: AndroidServiceFactory): ServiceFactory = androidServiceFactory

   @Provides
   fun provideDiskCacheManager(
      context: Context,
      errorReporter: ErrorReporter,
   ): GlobalOkHttpDiskCacheManager {
      return GlobalOkHttpDiskCacheManager(context, errorReporter)
   }

   @Provides
   @SingleIn(AppScope::class)
   fun provideCertificateTransparencyDiskCache(context: Context): DiskCache {
      return AndroidDiskCache(context)
   }
}
