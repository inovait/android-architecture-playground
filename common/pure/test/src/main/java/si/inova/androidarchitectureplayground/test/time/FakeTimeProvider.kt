package si.inova.androidarchitectureplayground.test.time

import si.inova.androidarchitectureplayground.common.time.TimeProvider
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * Time provider that provides fake pre-determined time. Use for tests.
 */
class FakeTimeProvider(
   private val currentLocalDate: () -> LocalDate = { LocalDate.MIN },
   private val currentLocalTime: () -> LocalTime = { LocalTime.MIN },
   private val currentLocalDateTime: () -> LocalDateTime = { LocalDateTime.of(currentLocalDate(), currentLocalTime()) },
   private val currentTimezone: () -> ZoneId = { ZoneId.of("UTC") },
   private val currentZonedDateTime: () -> ZonedDateTime = { ZonedDateTime.of(currentLocalDateTime(), currentTimezone()) },
   private val currentMilliseconds: () -> Long = { 0 }
) : TimeProvider {
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
