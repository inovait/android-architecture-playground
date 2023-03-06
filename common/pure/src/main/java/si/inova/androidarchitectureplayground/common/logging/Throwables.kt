package si.inova.androidarchitectureplayground.common.logging

import java.io.PrintWriter
import java.io.StringWriter

/**
 * Utility to turn a [Throwable] into a loggable string.
 *
 * The implementation is based on Timber.getStackTraceString(). It's different
 * from android.util.Log.getStackTraceString in the following ways:
 * - No silent swallowing of UnknownHostException.
 * - The buffer size is 256 bytes instead of the default 16 bytes.
 *
 * From https://github.com/square/logcat/blob/f877f806e8dce98a3abbc2ca80e6447b38cd8f42/logcat/src/main/java/logcat/Throwables.kt
 */
fun Throwable.asLog(): String {
   val stringWriter = StringWriter(DEFAULT_WRITER_SIZE)
   val printWriter = PrintWriter(stringWriter, false)
   printStackTrace(printWriter)
   printWriter.flush()
   return stringWriter.toString()
}

private const val DEFAULT_WRITER_SIZE = 256
