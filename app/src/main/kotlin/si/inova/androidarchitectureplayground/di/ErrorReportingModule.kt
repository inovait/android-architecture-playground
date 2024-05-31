package si.inova.androidarchitectureplayground.di

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import si.inova.androidarchitectureplayground.BuildConfig
import si.inova.androidarchitectureplayground.common.di.ApplicationScope
import si.inova.androidarchitectureplayground.common.exceptions.CrashOnDebugException
import si.inova.kotlinova.core.logging.logcat
import si.inova.kotlinova.core.outcome.CauseException
import si.inova.kotlinova.core.reporting.ErrorReporter
import kotlin.coroutines.cancellation.CancellationException

@Suppress("unused")
@ContributesTo(ApplicationScope::class)
@Module
class ErrorReportingModule {
   @Provides
   fun provideErrorReporter(): ErrorReporter {
      return object : ErrorReporter {
         override fun report(throwable: Throwable) {
            if (throwable is CancellationException) {
               report(Exception("Got cancellation exception", throwable))
               return
            }

            if (throwable !is CauseException || throwable.shouldReport) {
               logcat { "Reporting $throwable to Firebase" }
               // TODO Substitute with error reporter here (Firebase?)
               throwable.printStackTrace()
            } else if (BuildConfig.DEBUG) {
               if (throwable is CrashOnDebugException) {
                  throw throwable
               }
               throwable.printStackTrace()
            }
         }
      }
   }
}
