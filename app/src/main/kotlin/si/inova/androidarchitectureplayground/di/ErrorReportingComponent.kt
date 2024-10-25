package si.inova.androidarchitectureplayground.di

import me.tatarka.inject.annotations.Provides
import si.inova.androidarchitectureplayground.BuildConfig
import si.inova.androidarchitectureplayground.common.exceptions.CrashOnDebugException
import si.inova.kotlinova.core.exceptions.UnknownCauseException
import si.inova.kotlinova.core.logging.logcat
import si.inova.kotlinova.core.outcome.CauseException
import si.inova.kotlinova.core.reporting.ErrorReporter
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

@Suppress("unused")
@ContributesTo(AppScope::class)
interface ErrorReportingComponent {
   @Provides
   fun provideErrorReporter(): ErrorReporter {
      return object : ErrorReporter {
         override fun report(throwable: Throwable) {
            if (throwable !is CauseException) {
               report(UnknownCauseException("Got reported non-cause exception", throwable))
               return
            }

            if (throwable.shouldReport) {
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
