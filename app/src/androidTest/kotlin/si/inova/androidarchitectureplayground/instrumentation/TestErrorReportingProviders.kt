package si.inova.androidarchitectureplayground.instrumentation

import android.util.Log
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo
import dev.zacsweers.metro.Provides
import si.inova.kotlinova.core.outcome.CauseException
import si.inova.kotlinova.core.reporting.ErrorReporter

@ContributesTo(AppScope::class)
interface TestErrorReportingProviders {
   @Provides
   fun provideErrorReporter(): ErrorReporter {
      return ErrorReporter { e ->
         if (e !is CauseException || e.shouldReport) {
            Log.e("Testing", "Got unhandled exception", e)
            caughtExceptions += e
         } else {
            Log.w("Testing", "Got an exception", e)
         }
      }
   }

   companion object {
      val caughtExceptions = ArrayList<Throwable>()
   }
}
