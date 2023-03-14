package si.inova.androidarchitectureplayground.util

import androidx.compose.ui.test.IdlingResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import si.inova.androidarchitectureplayground.common.outcome.CoroutineResourceManager
import si.inova.androidarchitectureplayground.common.outcome.Outcome
import si.inova.androidarchitectureplayground.common.reporting.ErrorReporter
import java.util.Collections
import java.util.WeakHashMap
import kotlin.coroutines.CoroutineContext

object LoadingCountingIdlingResource : IdlingResource {
   private val flows = Collections.newSetFromMap<MutableStateFlow<*>>(WeakHashMap())

   fun registerResource(resource: MutableStateFlow<*>) {
      flows += resource
   }

   override val isIdleNow: Boolean
      get() {
         return !flows.any { it.value is Outcome.Progress<*> }
      }

   override fun getDiagnosticMessageIfBusy(): String {
      return "Resources ${flows.toList()} are busy"
   }
}

class RegisteringCoroutineResourceManager(scope: CoroutineScope, reportService: ErrorReporter) :
   CoroutineResourceManager(scope, reportService) {
   override fun <T> launchResourceControlTask(
      resource: MutableStateFlow<Outcome<T>>,
      currentValue: T?,
      context: CoroutineContext,
      block: suspend ResourceControlBlock<T>.() -> Unit
   ) {
      LoadingCountingIdlingResource.registerResource(resource)
      super.launchResourceControlTask(resource, currentValue, context, block)
   }
}
