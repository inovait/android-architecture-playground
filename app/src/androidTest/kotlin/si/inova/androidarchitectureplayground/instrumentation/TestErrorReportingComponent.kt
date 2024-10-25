package si.inova.androidarchitectureplayground.instrumentation

import android.util.Log
import me.tatarka.inject.annotations.Provides
import si.inova.kotlinova.core.outcome.CauseException
import si.inova.kotlinova.core.reporting.ErrorReporter
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

@ContributesTo(AppScope::class)
interface TestErrorReportingComponent {
   @Provides
   fun provideErrorReporter(): ErrorReporter {
      return ErrorReporter {
         if (it !is CauseException || it.shouldReport) {
            Log.e("Testing", "Got unhandled exception", it)
            caughtExceptions += it
         }
      }
   }

   companion object {
      val caughtExceptions = ArrayList<Throwable>()
   }
}
