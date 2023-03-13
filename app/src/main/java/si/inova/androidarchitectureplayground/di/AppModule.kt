package si.inova.androidarchitectureplayground.di

import android.app.Application
import android.content.Context
import com.androidarchitectureplayground.network.services.BaseServiceFactory
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.Multibinds
import dispatch.core.MainImmediateCoroutineScope
import si.inova.androidarchitectureplayground.common.logging.logcat
import si.inova.androidarchitectureplayground.common.outcome.CauseException
import si.inova.androidarchitectureplayground.common.outcome.CoroutineResourceManager
import si.inova.androidarchitectureplayground.common.reporting.ErrorReporter
import si.inova.androidarchitectureplayground.common.time.TimeProvider
import si.inova.androidarchitectureplayground.navigation.base.ConditionalNavigationHandler
import si.inova.androidarchitectureplayground.navigation.base.DeepLinkHandler
import si.inova.androidarchitectureplayground.time.AndroidDateTimeFormatter
import si.inova.androidarchitectureplayground.time.AndroidDateTimeFormatterImpl
import si.inova.androidarchitectureplayground.time.AndroidTimeProvider
import si.inova.androidarchitectureplayground.time.DefaultAndroidTimeProvider

@Suppress("unused")
@ContributesTo(ApplicationScope::class)
@Module(subcomponents = [NavigationStackComponent::class])
abstract class AppModule {
   @Binds
   abstract fun Application.bindToContext(): Context

   @Multibinds
   abstract fun provideDeepLinkHandlers(): Set<@JvmSuppressWildcards DeepLinkHandler>

   @Multibinds
   abstract fun provideConditionalNavigationHandlers():
      Map<@JvmSuppressWildcards Class<*>, @JvmSuppressWildcards ConditionalNavigationHandler>

   @Binds
   abstract fun AndroidTimeProvider.bindToTimeProvider(): TimeProvider

   @Binds
   abstract fun AndroidDateTimeFormatterImpl.bindToAndroidDateTimeFormatter(): AndroidDateTimeFormatter

   @Module
   companion object {
      @Provides
      fun provideMainCoroutineScope(): MainImmediateCoroutineScope {
         return MainImmediateCoroutineScope()
      }

      @Provides
      fun provideAndroidTimeProvider(): AndroidTimeProvider {
         return DefaultAndroidTimeProvider
      }

      @Provides
      fun provideDefaultCoroutineResourceManager(
         mainCoroutineScope: MainImmediateCoroutineScope,
         errorReporter: ErrorReporter
      ): CoroutineResourceManager {
         return CoroutineResourceManager(mainCoroutineScope, errorReporter)
      }

      @Provides
      fun provideErrorReporter(): ErrorReporter {
         return ErrorReporter {
            if (it !is CauseException || it.shouldReport) {
               logcat { "Reporting $it to Firebase" }
               it.printStackTrace()
            }
         }
      }

      @Provides
      @BaseServiceFactory.BaseUrl
      fun provideBaseUrl(): String {
         return "https://dummyjson.com/"
      }
   }
}
