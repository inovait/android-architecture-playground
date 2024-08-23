package si.inova.androidarchitectureplayground.common.pagination

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

/**
 * A [PaginatedDataStream] that uses a switchable backing flow to provide data.
 *
 * [load] is called whenever the data flow starts getting collected. To output data, set the value of the [targetFlow] property.
 * To wait for next page request from the user, you can call receive on the [nextPageChannel] channel.
 */
abstract class FlowSwitchingPaginatedDataStream<T> : PaginatedDataStream<T> {
   protected val nextPageChannel = Channel<Unit>(Channel.CONFLATED)
   protected val targetFlow: MutableStateFlow<Flow<PaginatedDataStream.PaginationResult<T>>> =
      MutableStateFlow(emptyFlow())

   override val data: Flow<PaginatedDataStream.PaginationResult<T>>
      get() = channelFlow<PaginatedDataStream.PaginationResult<T>> {
         try {
            coroutineScope {
               launch {
                  targetFlow.flatMapLatest { it }.collect(::send)
               }

               load()
            }
         } catch (ignored: CancellationException) {
         }
      }.distinctUntilChanged()

   protected abstract suspend fun load()

   override fun nextPage() {
      nextPageChannel.trySend(Unit)
   }
}
