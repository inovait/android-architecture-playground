package si.inova.androidarchitectureplayground.reporting

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import si.inova.androidarchitectureplayground.common.logging.ActionLogger
import si.inova.kotlinova.core.logging.logcat

@ContributesBinding(AppScope::class)
class DemoActionLogger @Inject constructor() : ActionLogger {
   override fun logAction(text: () -> String) {
      // TODO ideally here log actions somewhere where it can be useful (such as Firebase's Crashlytics)
      logcat(message = text)
   }
}
