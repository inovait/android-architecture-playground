package si.inova.androidarchitectureplayground.time

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Time provider that provides fake pre-determine time. Use for tests and for compose previews.
 */
class FakeAndroidTimeProvider(
   private val currentLocalDate: () -> LocalDate = { LocalDate.MIN },
   private val currentLocalTime: () -> LocalTime = { LocalTime.MIN },
   private val currentLocalDateTime: () -> LocalDateTime = { LocalDateTime.of(currentLocalDate(), currentLocalTime()) },
   private val currentTimezone: () -> ZoneId = { ZoneId.of("UTC") },
   private val currentZonedDateTime: () -> ZonedDateTime = { ZonedDateTime.of(currentLocalDateTime(), currentTimezone()) },
   private val currentMilliseconds: () -> Long = { 0 }
) : AndroidTimeProvider {
   override fun elapsedRealtime(): Long {
      return currentMilliseconds()
   }

   override fun elapsedRealtimeNanos(): Long {
      return currentMilliseconds()
   }

   override fun uptimeMillis(): Long {
      return currentMilliseconds()
   }

   override fun currentTimeMillis(): Long {
      return currentMilliseconds()
   }

   override fun currentLocalDate(): LocalDate {
      return currentLocalDate.invoke()
   }

   override fun currentLocalDateTime(): LocalDateTime {
      return currentLocalDateTime.invoke()
   }

   override fun currentLocalTime(): LocalTime {
      return currentLocalTime.invoke()
   }

   override fun currentInstant(): Instant {
      return Instant.ofEpochMilli(currentTimeMillis())
   }

   override fun currentZonedDateTime(): ZonedDateTime {
      return currentZonedDateTime.invoke()
   }

   override fun systemDefaultZoneId(): ZoneId {
      return currentTimezone()
   }
}
