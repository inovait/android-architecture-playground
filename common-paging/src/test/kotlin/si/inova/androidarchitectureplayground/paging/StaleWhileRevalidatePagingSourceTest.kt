package si.inova.androidarchitectureplayground.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter
import app.cash.turbine.test
import io.kotest.matchers.collections.shouldContainExactly
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.retrofit.ServiceTestingHelper

@OptIn(ExperimentalPagingApi::class)
class StaleWhileRevalidatePagingSourceTest {
   private val scope = TestScope()

   private val subsequentPageLoadInterceptor = ServiceTestingHelper()

   private val firstPageFlow = MutableSharedFlow<Outcome<List<Int>>>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

   private var wasLastLoadForced: Boolean? = null
   private var lastPageSize: Int? = null

   private val pagedList = staleWhileRevalidateInputPagingWrapper<Int>(
      pager = { pagingSourceFactory, mediator ->
         Pager(PagingConfig(pageSize = 2), remoteMediator = mediator, pagingSourceFactory = pagingSourceFactory)
      },
      getFirstPage = { force, pageSize ->
         lastPageSize = pageSize
         firstPageFlow
      },
      getSubsequentPage = { itemsLoadedSoFar, pageSize ->
         subsequentPageLoadInterceptor.intercept()
         lastPageSize = pageSize
         List(pageSize) { itemsLoadedSoFar + it }
      }
   )

   @BeforeEach
   fun setUp() {
      Dispatchers.setMain(scope.coroutineContext[CoroutineDispatcher]!!)
   }

   @AfterEach
   fun tearDown() {
      Dispatchers.resetMain()
   }

   @Test
   fun `Load first page at the start with triple pageSize`() = scope.runTest {
      val (pagingDataPresenter, dataFlow) = pagedList.getPresenterFlow()

      dataFlow.test {
         pagingDataPresenter.get(0)
         runCurrent()

         val items = expectMostRecentItem()
         items.shouldContainExactly(1, 2, 3, 4, 5, 6)
      }
   }
}

private fun <T : Any> Flow<PagingData<T>>.getPresenterFlow(): Pair<PagingDataPresenter<T>, Flow<List<T>>> {
   val presenter = object : PagingDataPresenter<T>() {
      override suspend fun presentPagingDataEvent(event: PagingDataEvent<T>) {
         println("event $event")
      }
   }



   return presenter to mapLatest {
      println("got paged list $it")
      presenter.collectFrom(it)
      presenter.snapshot().items
   }
}
