package si.inova.androidarchitectureplayground.instrumentation

import android.app.Application
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import si.inova.androidarchitectureplayground.di.ApplicationComponent
import si.inova.androidarchitectureplayground.di.CoroutinesComponent
import si.inova.androidarchitectureplayground.di.ErrorReportingComponent
import si.inova.androidarchitectureplayground.di.NetworkUrlComponent
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Component
@MergeComponent(
   AppScope::class,
   exclude = [CoroutinesComponent::class, ErrorReportingComponent::class, NetworkUrlComponent::class]
)
@SingleIn(AppScope::class)
abstract class TestApplicationComponent(
   @get:Provides
   val application: Application,
) : TestApplicationComponentMerged, ApplicationComponent
