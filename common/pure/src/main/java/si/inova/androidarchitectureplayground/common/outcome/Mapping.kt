package si.inova.androidarchitectureplayground.common.outcome

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * Map data of this outcome, while keeping the type.
 */
fun <A, B> Outcome<A>.mapData(mapper: (A) -> B): Outcome<B> {
   return when (this) {
      is Outcome.Error -> Outcome.Error(exception, data?.let { mapper(it) })
      is Outcome.Progress -> Outcome.Progress(data?.let { mapper(it) }, progress)
      is Outcome.Success -> Outcome.Success(mapper(data))
   }
}

/**
 * Returns a flow that switches to a new flow produced by transform function every time the original flow emits a value.
 * When the original flow emits a new value, the previous flow produced by transform block is cancelled.
 *
 * Unlike regular flatMapLatest, this one will attempt to properly merge outcome types. It will always take the worst outcome
 * type from the two (upstream and the one provided by the transform function):
 * * If any of the outcomes is [Outcome.Error], [Outcome.Error] is returned, with the data of [this].
 * * If any of the outcomes is [Outcome.Progress], [Outcome.Progress] is returned, with the data of [this].
 * * Otherwise, [Outcome.Success] is returned, with the data of [this].
 */
fun <A, B> Flow<Outcome<A>>.flatMapLatestOutcome(mapper: (A) -> Flow<Outcome<B>>): Flow<Outcome<B>> {
   return flatMapLatest { upstreamOutcome ->
      val data = upstreamOutcome.valueOrNull
      if (data == null) {
         @Suppress("UNCHECKED_CAST")
         return@flatMapLatest flowOf(upstreamOutcome as Outcome<B>)
      }

      val targetFlow = mapper(data)

      targetFlow.map { targetOutcome ->
         targetOutcome.downgradeTo(upstreamOutcome)
      }
   }
}

/**
 * Downgrade this outcome to the worst of the two:
 *
 * * If any of the outcomes is [Outcome.Error], [Outcome.Error] is returned, with the data of [this].
 * * If any of the outcomes is [Outcome.Progress], [Outcome.Progress] is returned, with the data of [this].
 * * Otherwise, [Outcome.Success] is returned, with the data of [this].
 */
fun <T> Outcome<T>.downgradeTo(
   targetType: Outcome<*>
): Outcome<T> {
   return when {
      this is Outcome.Error -> this
      targetType is Outcome.Error -> Outcome.Error(targetType.exception, valueOrNull)
      this is Outcome.Progress -> {
         if (targetType is Outcome.Progress) {
            val combinedProgress = targetType.progress?.let { progress?.times(it) }
            Outcome.Progress(data, combinedProgress)
         } else {
            this
         }
      }

      targetType is Outcome.Progress -> Outcome.Progress(valueOrNull, targetType.progress)
      else -> this
   }
}