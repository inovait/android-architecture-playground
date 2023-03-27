package si.inova.androidarchitectureplayground.common.pagination

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import si.inova.androidarchitectureplayground.common.outcome.LoadingStyle
import si.inova.androidarchitectureplayground.common.outcome.Outcome

/**
 * A [PaginatedDataStream] that loads individual pages based on the item offset.
 *
 * passed lambda will be called with offset = 0 for the first page. When next page is requested, passed lambda
 * will be called again with the offset of the next item
 * (e.g. if first page has 10 items, passed lambda will be called for second page with offset=11).
 */
class OffsetBasedPaginatedDataStream<T>(load: (nextOffset: Int) -> Flow<Outcome<List<T>>>) : PaginatedDataStream<List<T>> {
   private val nextPageChannel = Channel<Unit>()

   override val data: Flow<PaginatedDataStream.PaginationResult<List<T>>> = flow {
      var currentList = emptyList<T>()

      val firstPage = load(0)
      var anyDataLeft = true

      emitAll(
         firstPage
            .onEach { firstPageOutcome ->
               firstPageOutcome.data?.let { currentList = it }
            }
            .map { outcome -> PaginatedDataStream.PaginationResult(outcome, true) }
      )

      while (anyDataLeft && currentCoroutineContext().isActive) {
         nextPageChannel.receive()
         emit(
            PaginatedDataStream.PaginationResult(
               Outcome.Progress(currentList, style = LoadingStyle.ADDITIONAL_DATA),
               true
            )
         )

         val prevData = currentList
         val nextPage = load(currentList.size)
         emitAll(
            nextPage.map { outcome ->
               val resultOutcome = when (outcome) {
                  is Outcome.Error -> {
                     anyDataLeft = false
                     Outcome.Error(outcome.exception, currentList + (outcome.data ?: emptyList()))
                  }

                  is Outcome.Progress -> {
                     Outcome.Progress(
                        currentList + (outcome.data ?: emptyList()),
                        outcome.progress,
                        LoadingStyle.ADDITIONAL_DATA
                     )
                  }

                  is Outcome.Success -> {
                     currentList = prevData + outcome.data
                     if (outcome.data.isEmpty()) {
                        anyDataLeft = false
                     }
                     Outcome.Success(currentList)
                  }
               }

               PaginatedDataStream.PaginationResult(resultOutcome, anyDataLeft)
            }
         )
      }
   }

   override fun nextPage() {
      nextPageChannel.trySend(Unit)
   }
}
