package si.inova.androidarchitectureplayground.network.di

import android.content.Context
import com.appmattus.certificatetransparency.cache.AndroidDiskCache
import com.appmattus.certificatetransparency.cache.DiskCache
import me.tatarka.inject.annotations.Provides
import si.inova.androidarchitectureplayground.network.services.AndroidServiceFactory
import si.inova.androidarchitectureplayground.network.services.ServiceFactory
import si.inova.kotlinova.core.reporting.ErrorReporter
import si.inova.kotlinova.retrofit.caching.GlobalOkHttpDiskCacheManager
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@ContributesTo(AppScope::class)
interface AndroidNetworkComponent {
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
