package si.inova.androidarchitectureplayground.reporting

import com.squareup.anvil.annotations.ContributesBinding
import si.inova.androidarchitectureplayground.common.di.ApplicationScope
import si.inova.androidarchitectureplayground.common.logging.ActionLogger
import si.inova.kotlinova.core.logging.logcat
import javax.inject.Inject

@ContributesBinding(ApplicationScope::class)
class DemoActionLogger @Inject constructor() : ActionLogger {
   override fun logAction(text: () -> String) {
      // TODO ideally here log actions somewhere where it can be useful (such as Firebase's Crashlytics)
      logcat(message = text)
   }
}
