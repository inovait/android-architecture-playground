package si.inova.androidarchitectureplayground.di

import android.app.Application
import android.content.Context
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Binds
import dagger.Module
import dagger.Provides
import si.inova.androidarchitectureplayground.BuildConfig
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
@Module
abstract class AppModule {
   @Binds
   abstract fun Application.bindToContext(): Context

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
               // Substitute with actual Firebase reporting
               it.printStackTrace()
            } else if (BuildConfig.DEBUG) {
               it.printStackTrace()
            }
         }
      }
   }
}
