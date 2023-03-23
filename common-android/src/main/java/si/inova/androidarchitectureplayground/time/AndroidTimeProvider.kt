package si.inova.androidarchitectureplayground.time

import android.os.SystemClock
import androidx.compose.runtime.Stable
import si.inova.androidarchitectureplayground.common.time.TimeProvider

@Stable
interface AndroidTimeProvider : TimeProvider {
   /**
    * @return number of milliseconds since the system was booted, including deep sleep
    *
    * @see [SystemClock.elapsedRealtime]
    */
   fun elapsedRealtime(): Long

   /**
    * @return number of nanoseconds since the system was booted, including deep sleep
    *
    * @see [SystemClock.elapsedRealtimeNanos]
    */
   fun elapsedRealtimeNanos(): Long

   /**
    * @return number of nanoseconds since the system was booted, excluding deep sleep
    *
    * @see [SystemClock.uptimeMillis]
    */
   fun uptimeMillis(): Long
}
