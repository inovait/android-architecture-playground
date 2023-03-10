package si.inova.androidarchitectureplayground.common.outcome

import si.inova.androidarchitectureplayground.common.exceptions.UnknownCauseException

/**
 * Execute passed [block], returning its value, or [Outcome.Error] if block throws any exception
 */
inline fun <T> catchIntoOutcome(block: () -> Outcome<T>): Outcome<T> {
   return try {
      block()
   } catch (e: CauseException) {
      Outcome.Error(e)
   } catch (e: Exception) {
      Outcome.Error(UnknownCauseException(e))
   }
}
