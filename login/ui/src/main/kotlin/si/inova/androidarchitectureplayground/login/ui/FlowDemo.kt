package si.inova.androidarchitectureplayground.login.ui

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import si.inova.kotlinova.core.logging.logcat

object FlowDemo {
   suspend fun CoroutineScope.start() {
      val channel = Channel<Int>(Channel.BUFFERED)

      launch { getNumbers(channel) }

      channel.consumeEach {
         logcat { "Received $it" }
         delay(1_000)
         logcat { "Done receiving $it" }
      }
   }

   private suspend fun getNumbers(channel: Channel<Int>) {
      for (number in 0..5) {
         logcat { "Emit $number" }
         channel.send(number)
         logcat { "Done emitting $number" }
      }
   }
}
