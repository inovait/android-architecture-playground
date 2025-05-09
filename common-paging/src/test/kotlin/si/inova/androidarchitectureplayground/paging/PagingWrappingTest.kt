package si.inova.androidarchitectureplayground.paging

import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import app.cash.turbine.test
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import si.inova.kotlinova.core.exceptions.NoNetworkException
import si.inova.kotlinova.core.outcome.LoadingStyle
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.core.test.outcomes.shouldBeErrorWith
import si.inova.kotlinova.core.test.outcomes.shouldBeSuccessWithData

class PagingWrappingTest {
   @Test
   fun `Forward data`() = runTest {
      val pagingData = PagingData.from(
         listOf(1, 2, 3),
         LoadStates(
            refresh = LoadState.NotLoading(false),
            prepend = LoadState.NotLoading(false),
            append = LoadState.NotLoading(false)
         )
      )

      val pagingResult = flowOf(pagingData).toPagingResult(backgroundScope)
      pagingResult.data.test {
         runCurrent()

         expectMostRecentItem() shouldBeSuccessWithData pagedListOf(listOf(1, 2, 3))
      }
   }

   @Test
   fun `Forward data updates`() = runTest {
      val pagingData = PagingData.from(
         listOf(1, 2, 3),
         LoadStates(
            refresh = LoadState.NotLoading(false),
            prepend = LoadState.NotLoading(false),
            append = LoadState.NotLoading(false)
         )
      )

      val dataFlow = MutableStateFlow(pagingData)

      val pagingResult = dataFlow.toPagingResult(backgroundScope)
      pagingResult.data.test {
         dataFlow.value = PagingData.from(
            listOf(3, 2, 1),
            LoadStates(
               refresh = LoadState.NotLoading(false),
               prepend = LoadState.NotLoading(false),
               append = LoadState.NotLoading(false)
            )
         )
         runCurrent()

         expectMostRecentItem() shouldBeSuccessWithData pagedListOf(listOf(3, 2, 1))
      }
   }

   @Test
   fun `Forward loading with normal style when source is prepending`() = runTest {
      val pagingData = PagingData.from(
         listOf(1, 2, 3),
         LoadStates(
            refresh = LoadState.NotLoading(false),
            prepend = LoadState.Loading,
            append = LoadState.NotLoading(false)
         )
      )

      val pagingResult = flowOf(pagingData).toPagingResult(backgroundScope)
      pagingResult.data.test {
         runCurrent()

         expectMostRecentItem().shouldBeInstanceOf<Outcome.Progress<PagedList<Int>>>().apply {
            style shouldBe LoadingStyle.NORMAL
            data shouldBe pagedListOf(listOf(1, 2, 3))
         }
      }
   }

   @Test
   fun `Forward loading with refresh style when source is refreshing`() = runTest {
      val pagingData = PagingData.from(
         listOf(1, 2, 3),
         LoadStates(
            refresh = LoadState.Loading,
            prepend = LoadState.NotLoading(false),
            append = LoadState.NotLoading(false)
         )
      )

      val pagingResult = flowOf(pagingData).toPagingResult(backgroundScope)
      pagingResult.data.test {
         runCurrent()

         expectMostRecentItem().shouldBeInstanceOf<Outcome.Progress<PagedList<Int>>>().apply {
            style shouldBe LoadingStyle.USER_REQUESTED_REFRESH
            data shouldBe pagedListOf(listOf(1, 2, 3))
         }
      }
   }

   @Test
   fun `Forward loading with additional data style when source is appending`() = runTest {
      val pagingData = PagingData.from(
         listOf(1, 2, 3),
         LoadStates(
            refresh = LoadState.NotLoading(false),
            prepend = LoadState.NotLoading(false),
            append = LoadState.Loading
         )
      )

      val pagingResult = flowOf(pagingData).toPagingResult(backgroundScope)
      pagingResult.data.test {
         runCurrent()

         expectMostRecentItem().shouldBeInstanceOf<Outcome.Progress<PagedList<Int>>>().apply {
            style shouldBe LoadingStyle.ADDITIONAL_DATA
            data shouldBe pagedListOf(listOf(1, 2, 3))
         }
      }
   }

   @Test
   fun `Forward error when refresh fails`() = runTest {
      val error = NoNetworkException()

      val pagingData = PagingData.from(
         listOf(1, 2, 3),
         LoadStates(
            refresh = LoadState.Error(error),
            prepend = LoadState.NotLoading(false),
            append = LoadState.NotLoading(false)
         )
      )

      val pagingResult = flowOf(pagingData).toPagingResult(backgroundScope)
      pagingResult.data.test {
         runCurrent()

         expectMostRecentItem().shouldBeErrorWith(
            expectedData = pagedListOf(listOf(1, 2, 3)),
            exceptionType = NoNetworkException::class.java
         )
      }
   }

   @Test
   fun `Forward error when prepend fails`() = runTest {
      val error = NoNetworkException()

      val pagingData = PagingData.from(
         listOf(1, 2, 3),
         LoadStates(
            refresh = LoadState.NotLoading(false),
            prepend = LoadState.Error(error),
            append = LoadState.NotLoading(false)
         )
      )

      val pagingResult = flowOf(pagingData).toPagingResult(backgroundScope)
      pagingResult.data.test {
         runCurrent()

         expectMostRecentItem().shouldBeErrorWith(
            expectedData = pagedListOf(listOf(1, 2, 3)),
            exceptionType = NoNetworkException::class.java
         )
      }
   }

   @Test
   fun `Forward error when append fails`() = runTest {
      val error = NoNetworkException()

      val pagingData = PagingData.from(
         listOf(1, 2, 3),
         LoadStates(
            refresh = LoadState.NotLoading(false),
            prepend = LoadState.NotLoading(false),
            append = LoadState.Error(error),
         )
      )

      val pagingResult = flowOf(pagingData).toPagingResult(backgroundScope)
      pagingResult.data.test {
         runCurrent()

         expectMostRecentItem().shouldBeErrorWith(
            expectedData = pagedListOf(listOf(1, 2, 3)),
            exceptionType = NoNetworkException::class.java
         )
      }
   }
}
