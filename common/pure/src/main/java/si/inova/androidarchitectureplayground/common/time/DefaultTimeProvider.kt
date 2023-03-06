package si.inova.androidarchitectureplayground.common.time

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

object DefaultTimeProvider : TimeProvider {
   override fun currentTimeMillis(): Long {
      return System.currentTimeMillis()
   }

   override fun currentLocalDate(): LocalDate {
      return LocalDate.now()
   }

   override fun currentLocalDateTime(): LocalDateTime {
      return LocalDateTime.now()
   }

   override fun currentLocalTime(): LocalTime {
      return LocalTime.now()
   }

   override fun currentInstant(): Instant {
      return Instant.now()
   }

   override fun currentZonedDateTime(): ZonedDateTime {
      return ZonedDateTime.now()
   }

   override fun systemDefaultZoneId(): ZoneId {
      return ZoneId.systemDefault()
   }
}
