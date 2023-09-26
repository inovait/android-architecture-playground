package si.inova.androidarchitectureplayground.login.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import si.inova.kotlinova.core.logging.logcat

object FlowDemo {
   suspend fun CoroutineScope.start() {
      getFlow().collect {
         logcat { "Received $it" }
         delay(1_000)
      }
   }

   private fun getFlow(): Flow<Int> = flow {
      for (number in 0..5) {
         logcat { "Emit $number" }
         delay(1_000)
         emit(number)
      }
   }
}
