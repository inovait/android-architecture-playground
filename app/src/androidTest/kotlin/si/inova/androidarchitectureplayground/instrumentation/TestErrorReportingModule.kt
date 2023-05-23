package si.inova.androidarchitectureplayground.instrumentation

import android.util.Log
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import si.inova.androidarchitectureplayground.di.ErrorReportingModule
import si.inova.kotlinova.core.outcome.CauseException
import si.inova.kotlinova.core.reporting.ErrorReporter

@ContributesTo(ApplicationScope::class, replaces = [ErrorReportingModule::class])
@Module
object TestErrorReportingModule {
   val caughtExceptions = ArrayList<Throwable>()

   @Provides
   fun provideErrorReporter(): ErrorReporter {
      return ErrorReporter {
         if (it !is CauseException || it.shouldReport) {
            Log.e("Testing", "Got unhandled exception", it)
            caughtExceptions += it
         }
      }
   }
}
