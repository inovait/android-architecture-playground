package si.inova.androidarchitectureplayground.instrumentation

import android.app.Application
import dev.zacsweers.metro.Component
import dev.zacsweers.metro.Provides
import si.inova.androidarchitectureplayground.di.ApplicationGraph
import si.inova.androidarchitectureplayground.di.ErrorReportingProviders
import si.inova.androidarchitectureplayground.di.NetworkUrlProviders
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.MergeComponent
import dev.zacsweers.metro.SingleIn

@Component
@MergeComponent(
   AppScope::class,
   exclude = [ErrorReportingProviders::class, NetworkUrlProviders::class]
)
@SingleIn(AppScope::class)
abstract class TestApplicationGraph(
   @get:Provides
   val application: Application,
) : TestApplicationComponentMerged, ApplicationGraph
