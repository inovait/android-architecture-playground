package si.inova.androidarchitectureplayground.common.flow

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.selects.SelectBuilder
import kotlinx.coroutines.selects.select
import si.inova.androidarchitectureplayground.common.outcome.CauseException
import si.inova.androidarchitectureplayground.common.outcome.Outcome

/**
 * Returns a flow that will pass all data from the original flow, except that it will ensure that [Outcome.Progress] is never
 * emitted for a very short amount of time, that would cause blinking on the screen. It achieves that by:
 *
 * * Not emitting [Outcome.Progress] unless progress takes at least [waitThisLongToShowLoadingMs] milliseconds.
 *   If [doNotWaitForInterimLoadings] is set to true, this will only apply to the initial loading, not to the subsequent
 *   loadings that happen after a refresh.
 * * Once [Outcome.Progress] will be emitted, it will not emit any non-progress
 *   state for at least [keepLoadingActiveForAtLeastMs] milliseconds
 */
fun <T> Flow<Outcome<T>>.withBlinkingPrevention(
   waitThisLongToShowLoadingMs: Long = 100,
   keepLoadingActiveForAtLeastMs: Long = 500,
   doNotWaitForInterimLoadings: Boolean = false
): Flow<Outcome<T>> {
   return BlinkingPrevention(
      this,
      waitThisLongToShowLoadingMs,
      keepLoadingActiveForAtLeastMs,
      doNotWaitForInterimLoadings
   )
}

private class BlinkingPrevention<T>(
   private val upstream: Flow<Outcome<T>>,
   private val waitThisLongToShowLoadingMs: Long = 100,
   private val keepLoadingActiveForAtLeastMs: Long = 500,
   private val doNotWaitForInterimLoadings: Boolean = false
) : Flow<Outcome<T>> {
   var waitingToSeeIfLoadingJustFlashes = false
   var waitingForProlongedLoadingToFinish = false
   var lastData: T? = null
   var lastProgress: Float? = null
   var lastError: CauseException? = null
   var gotSuccessDuringLoading = false
   var gotAtLeastOneSuccess = false

   override suspend fun collect(collector: FlowCollector<Outcome<T>>) {
      flow {
         coroutineScope {
            val upstreamChannel = upstream.produceIn(this)

            while (isActive) {
               select<Unit> {
                  upstreamChannel.onReceive {
                     when (it) {
                        is Outcome.Progress -> {
                           lastData = it.data
                           lastProgress = it.progress

                           if (waitingForProlongedLoadingToFinish || (gotAtLeastOneSuccess && doNotWaitForInterimLoadings)) {
                              emit(Outcome.Progress(lastData, lastProgress))
                           } else if (waitingToSeeIfLoadingJustFlashes) {
                              // Do nothing, only update current data
                           } else {
                              waitingToSeeIfLoadingJustFlashes = true
                           }
                        }

                        is Outcome.Success -> {
                           gotAtLeastOneSuccess = true

                           if (waitingForProlongedLoadingToFinish) {
                              lastData = it.data
                              emit(Outcome.Progress(lastData, lastProgress))
                              gotSuccessDuringLoading = true
                           } else {
                              waitingToSeeIfLoadingJustFlashes = false
                              emit(it)
                           }
                        }

                        is Outcome.Error -> {
                           if (waitingForProlongedLoadingToFinish) {
                              lastData = it.data
                              lastError = it.exception
                              emit(Outcome.Progress(lastData, lastProgress))
                           } else {
                              waitingToSeeIfLoadingJustFlashes = false
                              lastError = null
                              emit(it)
                           }
                        }
                     }
                  }

                  if (waitingToSeeIfLoadingJustFlashes) {
                     showLoadingAfterTimeout(this, this@flow)
                  }

                  if (waitingForProlongedLoadingToFinish) {
                     continueAfterLoadingFinishes(this, this@flow)
                  }
               }
            }
         }
      }.collect(collector)
   }

   private fun continueAfterLoadingFinishes(
      selectBuilder: SelectBuilder<Unit>,
      flowCollector: FlowCollector<Outcome<T>>
   ) {
      selectBuilder.onTimeout(keepLoadingActiveForAtLeastMs) {
         val currentLastError = lastError
         if (currentLastError != null) {
            flowCollector.emit(Outcome.Error(currentLastError, lastData))
            lastError = null
         } else if (gotSuccessDuringLoading) {
            flowCollector.emit(Outcome.Success(requireNotNull(lastData)))
            gotSuccessDuringLoading = false
         }
         waitingForProlongedLoadingToFinish = false
      }
   }

   private fun showLoadingAfterTimeout(
      selectBuilder: SelectBuilder<Unit>,
      flowCollector: FlowCollector<Outcome<T>>
   ) {
      selectBuilder.onTimeout(waitThisLongToShowLoadingMs) {
         flowCollector.emit(Outcome.Progress(lastData, lastProgress))
         waitingToSeeIfLoadingJustFlashes = false
         waitingForProlongedLoadingToFinish = true
      }
   }
}
