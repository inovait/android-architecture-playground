package si.inova.androidarchitectureplayground.paging

import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import si.inova.kotlinova.core.exceptions.UnknownCauseException
import si.inova.kotlinova.core.outcome.CauseException
import si.inova.kotlinova.core.outcome.LoadingStyle
import si.inova.kotlinova.core.outcome.Outcome

/**
 * Transform [PagingData] into [PagingResult].
 */
fun <T : Any> Flow<PagingData<T>>.toPagingResult(coroutineScope: CoroutineScope): PagingResult<T> {
   val cached = cachedIn(coroutineScope) as SharedFlow<PagingData<T>>

   val pagingListFlow = MutableStateFlow<PagedList<T>?>(null)

   val presenter = object : PagingDataPresenter<T>(
      cachedPagingData = cached.replayCache.firstOrNull(),
      mainContext = coroutineScope.coroutineContext
   ) {
      override suspend fun presentPagingDataEvent(event: PagingDataEvent<T>) {
         pagingListFlow.value = PresenterPagedList(this)
      }
   }

   pagingListFlow.value = PresenterPagedList(presenter)

   val dataFlow: Flow<Outcome<PagedList<T>>> = flow {
      coroutineScope {
         launch {
            this@toPagingResult.collectLatest {
               presenter.collectFrom(it)
            }
         }

         emitAll(
            presenter.loadStateFlow.flatMapLatest { loadStates ->
               pagingListFlow.filterNotNull().map {
                  convertLoadStatesToOutcome(loadStates, it)
               }
            }

         )
      }
   }

   return object : PagingResult<T> {
      override val data: Flow<Outcome<PagedList<T>>>
         get() = dataFlow

      override fun refresh() {
         presenter.refresh()
      }

      override fun retry() {
         presenter.retry()
      }
   }
}

private fun <T : Any> convertLoadStatesToOutcome(
   loadStates: CombinedLoadStates?,
   data: PagedList<T>,
): Outcome<PagedList<T>> {
   if (loadStates == null) {
      return Outcome.Progress(data)
   }

   val append = loadStates.append
   val prepend = loadStates.prepend
   val refresh = loadStates.refresh

   return if (refresh is LoadState.Loading) {
      Outcome.Progress(data, style = LoadingStyle.USER_REQUESTED_REFRESH)
   } else if (append is LoadState.Loading) {
      Outcome.Progress(data, style = LoadingStyle.ADDITIONAL_DATA)
   } else if (prepend is LoadState.Loading) {
      Outcome.Progress(data)
   } else if (append is LoadState.Error) {
      Outcome.Error(append.error.toCauseException(), data = data)
   } else if (refresh is LoadState.Error) {
      Outcome.Error(refresh.error.toCauseException(), data = data)
   } else if (prepend is LoadState.Error) {
      Outcome.Error(prepend.error.toCauseException(), data = data)
   } else {
      Outcome.Success(data)
   }
}

private class PresenterPagedList<T : Any>(
   private val presenter: PagingDataPresenter<T>,
) : ListBackedPagedListImpl<T>(presenter.snapshot()) {
   override fun get(index: Int): T? {
      if (index >= presenter.size) {
         // Race condition: flow has not updated yet, so we might receive data that is not there yet.
         // Return null for now
         return null
      }
      presenter[index]
      return super[index]
   }
}

private fun Throwable.toCauseException(): CauseException {
   return if (this is CauseException) {
      this
   } else {
      UnknownCauseException(cause = this)
   }
}
