package si.inova.androidarchitectureplayground.login.ui

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import si.inova.kotlinova.core.logging.logcat
import java.io.File

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

class Logger(private val context: Context, private val coroutineScope: CoroutineScope) {
   private val channel = Channel<String>(Channel.BUFFERED)

   fun log(text: String) {
      channel.trySend(text)
   }

   init {
      coroutineScope.launch(Dispatchers.Default) {
         val file = File(context.filesDir, "log.txt")

         channel.consumeEach {
            file.appendText(it)
         }
      }
   }
}
