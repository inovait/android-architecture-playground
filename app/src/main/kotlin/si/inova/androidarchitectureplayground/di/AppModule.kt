package si.inova.androidarchitectureplayground.di

import android.app.Application
import android.content.Context
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.Multibinds
import si.inova.androidarchitectureplayground.BuildConfig
import si.inova.androidarchitectureplayground.navigation.base.ConditionalNavigationHandler
import si.inova.androidarchitectureplayground.navigation.base.DeepLinkHandler
import si.inova.kotlinova.core.logging.logcat
import si.inova.kotlinova.core.outcome.CauseException
import si.inova.kotlinova.core.reporting.ErrorReporter
import si.inova.kotlinova.core.time.AndroidDateTimeFormatter
import si.inova.kotlinova.core.time.AndroidDateTimeFormatterImpl
import si.inova.kotlinova.core.time.AndroidTimeProvider
import si.inova.kotlinova.core.time.DefaultAndroidTimeProvider
import si.inova.kotlinova.core.time.TimeProvider

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
      fun provideAndroidTimeProvider(): AndroidTimeProvider {
         return DefaultAndroidTimeProvider
      }

      @Provides
      fun provideErrorReporter(): ErrorReporter {
         return ErrorReporter {
            if (it !is CauseException || it.shouldReport) {
               logcat { "Reporting $it to Firebase" }
               it.printStackTrace()
            } else if (BuildConfig.DEBUG) {
               it.printStackTrace()
            }
         }
      }
   }
}
