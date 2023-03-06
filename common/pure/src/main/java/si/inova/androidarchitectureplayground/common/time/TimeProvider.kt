package si.inova.androidarchitectureplayground.common.time

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

interface TimeProvider {
   /**
    * @return difference, measured in milliseconds, between the current time and midnight, January 1, 1970 UTC.
    *
    * @see System.currentTimeMillis
    */
   fun currentTimeMillis(): Long

   fun currentLocalDate(): LocalDate

   fun currentLocalDateTime(): LocalDateTime

   fun currentLocalTime(): LocalTime

   fun currentInstant(): Instant

   fun currentZonedDateTime(): ZonedDateTime

   fun systemDefaultZoneId(): ZoneId
}
