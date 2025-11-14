package si.inova.androidarchitectureplayground.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.Pager
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import si.inova.kotlinova.core.outcome.Outcome

/**
 * Memory cached paging wrapper that loads first page using Stale-while-revalidate call and all subsequent
 * pages using normal calls
 */
@OptIn(ExperimentalPagingApi::class)
fun <Value : Any> staleWhileRevalidateInputPagingWrapper(
   pager: ((pagingSource: () -> PagingSource<Int, Value>, remoteMediator: RemoteMediator<Int, Value>) -> Pager<Int, Value>),
   getFirstPage: (force: Boolean, pageSize: Int) -> Flow<Outcome<List<Value>>>,
   getSubsequentPage: suspend (itemsLoadedSoFar: Int, pageSize: Int) -> List<Value>,
): Flow<PagingData<Value>> {
   return flow {
      coroutineScope {
         var data: List<Value> = emptyList()
         var currentPagingSource: PagingSource<Int, Value>? = null

         val pagerFactory: () -> PagingSource<Int, Value> = {
            object : PagingSource<Int, Value>() {
               override fun getRefreshKey(state: PagingState<Int, Value>): Int? {
                  return state.anchorPosition?.let { maxOf(0, it - (state.config.initialLoadSize / 2)) }
               }

               override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Value> {
                  println("load $params ${params.key} ${params.loadSize} ${data.size}")
                  val key = params.key ?: 0

                  val limit = when (params) {
                     is PagingSource.LoadParams.Prepend<*> -> minOf(key, params.loadSize)
                     else -> params.loadSize
                  }

                  val totalCount = data.size

                  val offset = when (params) {
                     is PagingSource.LoadParams.Prepend<*> -> maxOf(0, key - params.loadSize)
                     is PagingSource.LoadParams.Append<*> -> key
                     is PagingSource.LoadParams.Refresh<*> -> if (key >= totalCount - params.loadSize) {
                        maxOf(
                           0,
                           totalCount - params.loadSize
                        )
                     } else {
                        key
                     }
                  }
                  println("subData $offset $limit $totalCount")
                  val subData = data.subList(offset, (offset + limit).coerceAtMost(data.size))

                  val nextPosToLoad = offset + subData.size
                  return PagingSource.LoadResult.Page(
                     data = subData,
                     prevKey = offset.takeIf { it > 0 && subData.isNotEmpty() },
                     nextKey = nextPosToLoad.takeIf { subData.isNotEmpty() && subData.size >= 0 && it < totalCount },
                     itemsBefore = offset,
                     itemsAfter = maxOf(0, totalCount - nextPosToLoad),
                  )
               }
            }.also { currentPagingSource = it }
         }

         val mediator = object : RemoteMediator<Int, Value>() {
            override suspend fun load(
               loadType: LoadType,
               state: PagingState<Int, Value>,
            ): MediatorResult {
               println("loadType $loadType ${state.pages.size} ${state.pages.map { it.prevKey to it.nextKey }}")
               return when (loadType) {
                  LoadType.PREPEND -> {
                     // Not supported
                     MediatorResult.Success(endOfPaginationReached = true)
                  }

                  LoadType.APPEND -> {
                     val newData = try {
                        getSubsequentPage(data.size, state.config.pageSize)
                     } catch (e: CancellationException) {
                        throw e
                     } catch (e: Throwable) {
                        return MediatorResult.Error(e)
                     }

                     data = data + newData
                     if (newData.isNotEmpty()) {
                        currentPagingSource?.invalidate()
                     }
                     MediatorResult.Success(newData.isEmpty())
                  }

                  LoadType.REFRESH -> {
                     // When pages is not empty and REFRESH is called, it means user requested pull to refresh
                     val force = state.pages.isNotEmpty()

                     getFirstPage(force, state.config.initialLoadSize).collect {
                        val newData = it.data.orEmpty()
                        if (newData != data) {
                           data = newData
                           currentPagingSource?.invalidate()
                        }
                     }

                     MediatorResult.Success(data.isEmpty())
                  }
               }
            }
         }

         val pager = pager(
            pagerFactory,
            mediator
         )

         emitAll(pager.flow)
      }
   }
}
