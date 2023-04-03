package si.inova.androidarchitectureplayground.network.di

import android.content.Context
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import dagger.Provides
import si.inova.androidarchitectureplayground.network.services.AndroidServiceFactory
import si.inova.androidarchitectureplayground.network.services.ServiceFactory
import si.inova.kotlinova.core.di.PureApplicationScope
import si.inova.kotlinova.core.reporting.ErrorReporter
import si.inova.kotlinova.retrofit.caching.GlobalOkHttpDiskCacheManager

@Module
@ContributesTo(PureApplicationScope::class)
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
   }
}
