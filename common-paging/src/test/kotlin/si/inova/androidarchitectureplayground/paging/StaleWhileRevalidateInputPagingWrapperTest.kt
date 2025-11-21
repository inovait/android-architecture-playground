package si.inova.androidarchitectureplayground.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter
import app.cash.turbine.test
import app.cash.turbine.turbineScope
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.retrofit.ServiceTestingHelper

@OptIn(ExperimentalPagingApi::class)
class StaleWhileRevalidateInputPagingWrapperTest {
   private val scope = TestScope()

   private val subsequentPageLoadInterceptor = ServiceTestingHelper()
   private var lastPageSize: Int? = null

   private var getFirstPage: (force: Boolean, pageSize: Int) -> Flow<Outcome<List<Int>>> = { _, pageSize ->
      lastPageSize = pageSize
      flowOf(Outcome.Success(List(pageSize) { it + 1 }))
   }

   private val pagedList = StaleWhileRevalidateInputPagingWrapper<Int>(
      pager = { pagingSourceFactory, mediator ->
         Pager(PagingConfig(pageSize = 2), remoteMediator = mediator, pagingSourceFactory = pagingSourceFactory)
      },
      getFirstPage = { force, pageSize -> getFirstPage(force, pageSize) },
      getSubsequentPage = { itemsLoadedSoFar, pageSize ->
         subsequentPageLoadInterceptor.intercept()
         lastPageSize = pageSize
         List(pageSize) { itemsLoadedSoFar + 1 + it }
      }
   ).flow

   @Test
   fun `Load first page at the start with triple pageSize`() = scope.runTest {
      val (pagingDataPresenter, dataFlow) = getPresenterFlow()

      dataFlow.test {
         runCurrent()
         pagingDataPresenter.get(0)
         runCurrent()

         val items = expectMostRecentItem()
         items.shouldContainExactly(1, 2, 3, 4, 5, 6)
      }
   }

   @Test
   fun `When user gets to the end, load next page`() = scope.runTest {
      val (pagingDataPresenter, dataFlow) = getPresenterFlow()

      dataFlow.test {
         runCurrent()
         pagingDataPresenter.get(5)
         runCurrent()

         val items = expectMostRecentItem()
         items.shouldContainExactly(3, 4, 5, 6, 7, 8)
      }
   }

   @Test
   fun `When loading first page as stale while revalidate, emit loading first`() = scope.runTest {
      val continueWithLoading = CompletableDeferred<Unit>()

      turbineScope {
         getFirstPage = { _, pageSize ->
            lastPageSize = pageSize
            flow {
               emit(Outcome.Progress(List(pageSize) { it + 1 }))
               continueWithLoading.await()
               emit(Outcome.Success(List(pageSize) { it + 1 }))
            }
         }

         val (pagingDataPresenter, dataFlow) = getPresenterFlow()

         val dataTurbine = dataFlow.testIn(backgroundScope)

         pagingDataPresenter.loadStateFlow.test {
            runCurrent()
            expectMostRecentItem()?.mediator?.refresh shouldBe LoadState.Loading

            continueWithLoading.complete(Unit)
            runCurrent()
            expectMostRecentItem()?.mediator?.refresh shouldBe LoadState.NotLoading(false)

            cancelAndIgnoreRemainingEvents()
         }

         dataTurbine.cancelAndIgnoreRemainingEvents()
      }
   }

   @Test
   fun `When loading first page as stale while revalidate, emit updated items`() = scope.runTest {
      val continueWithLoading = CompletableDeferred<Unit>()

      turbineScope {
         getFirstPage = { _, pageSize ->
            lastPageSize = pageSize
            flow {
               emit(Outcome.Progress(List(pageSize) { it + 1 }))
               continueWithLoading.await()
               emit(Outcome.Success(List(pageSize) { it + 2 }))
            }
         }

         val (_, dataFlow) = getPresenterFlow()

         dataFlow.test {
            runCurrent()
            expectMostRecentItem().shouldContainExactly(1, 2, 3, 4, 5, 6)

            continueWithLoading.complete(Unit)
            runCurrent()
            expectMostRecentItem().shouldContainExactly(2, 3, 4, 5, 6, 7)
         }
      }
   }

   private fun getPresenterFlow(): Pair<PagingDataPresenter<Int>, Flow<List<Int>>> {
      val dataFlow = MutableStateFlow<List<Int>>(emptyList())
      val presenter = object : PagingDataPresenter<Int>(scope.backgroundScope.coroutineContext) {
         override suspend fun presentPagingDataEvent(event: PagingDataEvent<Int>) {
            dataFlow.value = snapshot().items
         }
      }
      val emitterFlow = flow {
         coroutineScope {
            launch {
               pagedList.collectLatest { presenter.collectFrom(it) }
            }

            emitAll(dataFlow)
         }
      }
      return presenter to emitterFlow
   }
}
