package si.inova.androidarchitectureplayground.test.time

import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.currentTime
import si.inova.androidarchitectureplayground.common.time.TimeProvider
import si.inova.androidarchitectureplayground.time.AndroidTimeProvider
import si.inova.androidarchitectureplayground.time.FakeAndroidTimeProvider
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

/**
 * A [TimeProvider] instance that provides time based on virtual clock of this kotlin test.
 */
fun TestScope.virtualAndroidTimeProvider(
   currentLocalDate: () -> LocalDate = { LocalDate.MIN },
   currentLocalTime: () -> LocalTime = { LocalTime.MIN },
   currentLocalDateTime: () -> LocalDateTime = { LocalDateTime.of(currentLocalDate(), currentLocalTime()) },
   currentTimezone: () -> ZoneId = { ZoneId.of("UTC") },
   currentZonedDateTime: (() -> ZonedDateTime)? = null
): AndroidTimeProvider {
   return FakeAndroidTimeProvider(
      currentLocalDate,
      currentLocalTime,
      currentLocalDateTime,
      currentTimezone,
      currentZonedDateTime = currentZonedDateTime ?: {
         ZonedDateTime.ofInstant(
            Instant.ofEpochMilli(currentTime),
            currentTimezone()
         )
      },
      currentMilliseconds = { currentTime },
   )
}
