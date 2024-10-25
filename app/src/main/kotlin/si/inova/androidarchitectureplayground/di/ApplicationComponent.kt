package si.inova.androidarchitectureplayground.di

import android.app.Application
import dispatch.core.DefaultCoroutineScope
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import si.inova.kotlinova.core.reporting.ErrorReporter
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@Component
@MergeComponent(AppScope::class)
@SingleIn(AppScope::class)
abstract class MainApplicationComponent(
   @get:Provides
   val application: Application,
) : ApplicationComponent, MainApplicationComponentMerged

interface ApplicationComponent : NavigationSubComponent.Factory {
   fun getErrorReporter(): ErrorReporter
   fun getDefaultCoroutineScope(): DefaultCoroutineScope
}
