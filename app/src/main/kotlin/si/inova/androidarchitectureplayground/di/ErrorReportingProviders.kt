package si.inova.androidarchitectureplayground.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import logcat.logcat
import si.inova.androidarchitectureplayground.BuildConfig
import si.inova.androidarchitectureplayground.common.exceptions.CrashOnDebugException
import si.inova.kotlinova.core.exceptions.UnknownCauseException
import si.inova.kotlinova.core.outcome.CauseException
import si.inova.kotlinova.core.reporting.ErrorReporter

@Suppress("unused")
@ContributesTo(AppScope::class)
interface ErrorReportingProviders {
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
               // Substitute with actual Firebase reporting
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
