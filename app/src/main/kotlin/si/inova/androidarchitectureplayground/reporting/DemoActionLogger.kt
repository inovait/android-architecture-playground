package si.inova.androidarchitectureplayground.reporting

import me.tatarka.inject.annotations.Inject
import si.inova.androidarchitectureplayground.common.logging.ActionLogger
import si.inova.kotlinova.core.logging.logcat
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@ContributesBinding(AppScope::class)
class DemoActionLogger @Inject constructor() : ActionLogger {
   override fun logAction(text: () -> String) {
      // TODO ideally here log actions somewhere where it can be useful (such as Firebase's Crashlytics)
      logcat(message = text)
   }
}
