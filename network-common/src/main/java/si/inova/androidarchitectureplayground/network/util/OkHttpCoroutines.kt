package si.inova.androidarchitectureplayground.network.util

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun Call.enqueueAndAwait(): Response {
   return suspendCancellableCoroutine { continuation ->
      enqueue(object : Callback {
         override fun onFailure(call: Call, e: IOException) {
            continuation.resumeWithException(e)
         }

         override fun onResponse(call: Call, response: Response) {
            continuation.resume(response)
         }
      })

      continuation.invokeOnCancellation {
         cancel()
      }
   }
}
