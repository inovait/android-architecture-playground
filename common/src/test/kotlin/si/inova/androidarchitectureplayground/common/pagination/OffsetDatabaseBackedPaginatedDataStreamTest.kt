package si.inova.androidarchitectureplayground.common.pagination

import app.cash.turbine.test
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.test.runCurrent
import org.junit.jupiter.api.Test
import si.inova.androidarchitectureplayground.common.pagination.OffsetDatabaseBackedPaginatedDataStream.DatabaseResult
import si.inova.androidarchitectureplayground.common.test.datastore.runTestWithDispatchers
import si.inova.kotlinova.core.exceptions.NoNetworkException
import si.inova.kotlinova.core.outcome.LoadingStyle
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.core.outcome.catchIntoOutcome
import si.inova.kotlinova.core.test.outcomes.shouldBeErrorWith
import si.inova.kotlinova.core.test.outcomes.shouldBeProgressWithData
import si.inova.kotlinova.core.test.outcomes.shouldBeSuccessWithData
import si.inova.kotlinova.retrofit.InterceptionStyle
import si.inova.kotlinova.retrofit.ServiceTestingHelper

class OffsetDatabaseBackedPaginatedDataStreamTest {
   private val database =
      MutableStateFlow(DatabaseResult<Int>(emptyList(), true))

   private
   val networkInterceptor = ServiceTestingHelper()

   private val providedNumbers = createNumbers(0, 30)

   private val networkCallsMade = ArrayList<IntRange>()

   private fun createDataStream(): OffsetDatabaseBackedPaginatedDataStream<Int> {
      return OffsetDatabaseBackedPaginatedDataStream(
         loadFromNetwork = { offset, limit ->
            networkCallsMade += offset until offset + limit

            catchIntoOutcome {
               networkInterceptor.intercept()
               Outcome.Success(providedNumbers.drop(offset).take(limit))
            }
         },
         loadFromDatabase = { offset: Int, limit: Int ->
            database.map { dbEntry ->
               Outcome.Success(dbEntry.copy(items = dbEntry.items.drop(offset).take(limit)))
            }
         },
         saveToDatabase = { data, replaceExisting ->
            if (replaceExisting) {
               database.update { it.copy(items = data) }
            } else {
               database.update { it.copy(items = it.items + data) }
            }
         },
      )
   }

   @Test
   fun `Provide first page of items from network`() = runTestWithDispatchers {
      val dataStream = createDataStream()

      dataStream.data.test {
         runCurrent()
         val numbers = expectMostRecentItem()

         numbers.items shouldBeSuccessWithData createNumbers(0, 10)
      }
   }

   @Test
   fun `Provide second page of items from network`() = runTestWithDispatchers {
      val expectedNumbers = createNumbers(0, 20)

      val dataStream = createDataStream()
      dataStream.data.test {
         runCurrent()
         dataStream.nextPage()
         runCurrent()

         val numbers = expectMostRecentItem()

         numbers.items shouldBeSuccessWithData expectedNumbers
      }
   }

   @Test
   fun `Provide first page from cached database`() = runTestWithDispatchers {
      val cachingDataStream = createDataStream()
      cachingDataStream.data.first() // Load data into database

      networkInterceptor.interceptAllFutureCallsWith(
         InterceptionStyle.Error(
            IllegalStateException("Network should not be called now")
         )
      )

      val dataStream = createDataStream()

      dataStream.data.test {
         runCurrent()

         val numbers = expectMostRecentItem()

         numbers.items shouldBeSuccessWithData createNumbers(0, 10)
      }
   }

   @Test
   fun `Provide second page from cached database`() = runTestWithDispatchers {
      val cachingDataStream = createDataStream()
      cachingDataStream.data.test {
         runCurrent()
         cachingDataStream.nextPage()
         runCurrent()
         cancelAndConsumeRemainingEvents()
      }

      networkInterceptor.interceptAllFutureCallsWith(
         InterceptionStyle.Error(
            AssertionError("Network should not be called now")
         )
      )

      val dataStream = createDataStream()

      dataStream.data.test {
         runCurrent()
         dataStream.nextPage()
         runCurrent()

         val numbers = expectMostRecentItem()

         numbers.items shouldBeSuccessWithData createNumbers(0, 20)
      }
   }

   @Test
   fun `Provide subsequent page from network if database is not ready`() = runTestWithDispatchers {
      val cachingDataStream = createDataStream()
      cachingDataStream.data.first() // Load data into database

      val dataStream = createDataStream()

      dataStream.data.test {
         runCurrent()
         dataStream.nextPage()
         runCurrent()

         val numbers = expectMostRecentItem()

         numbers.items shouldBeSuccessWithData createNumbers(0, 20)
      }
   }

   @Test
   fun `Provide proper value for has any data left`() = runTestWithDispatchers {
      val cachingDataStream = createDataStream()
      cachingDataStream.data.first() // Load data into database

      val dataStream = createDataStream()

      dataStream.data.test {
         runCurrent()
         expectMostRecentItem().hasAnyDataLeft.shouldBeTrue()

         dataStream.nextPage()
         runCurrent()
         expectMostRecentItem().hasAnyDataLeft.shouldBeTrue()

         dataStream.nextPage()
         runCurrent()
         expectMostRecentItem().hasAnyDataLeft.shouldBeTrue()

         dataStream.nextPage()
         runCurrent()
         expectMostRecentItem().hasAnyDataLeft.shouldBeFalse()
      }
   }

   @Test
   fun `When cache is expired, show expired data first as Loading while fetching in the background`() =
      runTestWithDispatchers {
         val cachingDataStream = createDataStream()
         cachingDataStream.data.first() // Load data into database
         runCurrent()

         database.update { it.copy(isCacheStillValid = false) }

         val dataStream = createDataStream()

         dataStream.data.test {
            awaitItem().items shouldBeProgressWithData createNumbers(0, 10)
            awaitItem().items shouldBeSuccessWithData createNumbers(0, 10)
         }
      }

   @Test
   fun `Show loading of subsequent pages with additional data style`() =
      runTestWithDispatchers {
         networkInterceptor.interceptAllFutureCallsWith(InterceptionStyle.InfiniteLoad)

         val dataStream = createDataStream()

         dataStream.data.test {
            runCurrent()
            expectMostRecentItem().items shouldBe Outcome.Progress(emptyList(), style = LoadingStyle.NORMAL)

            networkInterceptor.completeInfiniteLoad()
            runCurrent()
            dataStream.nextPage()
            runCurrent()

            expectMostRecentItem().items shouldBe Outcome.Progress(createNumbers(0, 10), style = LoadingStyle.ADDITIONAL_DATA)

            networkInterceptor.completeInfiniteLoad()
            runCurrent()
            dataStream.nextPage()
            runCurrent()

            expectMostRecentItem().items shouldBe Outcome.Progress(createNumbers(0, 20), style = LoadingStyle.ADDITIONAL_DATA)
         }
      }

   @Test
   fun `Do not make more calls when data ends`() = runTestWithDispatchers {
      val dataStream = createDataStream()

      dataStream.data.test {
         repeat(20) {
            runCurrent()
            dataStream.nextPage()
         }

         runCurrent()
         cancelAndConsumeRemainingEvents()
      }

      networkCallsMade.shouldContainExactly(
         0 until 10,
         10 until 20,
         20 until 30,
         30 until 40,
      )
   }

   @Test
   fun `Forward network errors to output for the first page`() = runTestWithDispatchers {
      val dataStream = createDataStream()

      networkInterceptor.interceptAllFutureCallsWith(InterceptionStyle.Error(NoNetworkException()))

      dataStream.data.test {
         runCurrent()
         expectMostRecentItem().apply {
            items.shouldBeErrorWith(exceptionType = NoNetworkException::class.java)
            hasAnyDataLeft.shouldBeFalse()
         }

         cancelAndConsumeRemainingEvents()
      }
   }

   @Test
   fun `Forward network errors to output for the second page`() = runTestWithDispatchers {
      val cachingDataStream = createDataStream()
      cachingDataStream.data.first() // Load data into database

      val dataStream = createDataStream()

      networkInterceptor.interceptAllFutureCallsWith(InterceptionStyle.Error(NoNetworkException()))

      dataStream.data.test {
         runCurrent()
         dataStream.nextPage()
         runCurrent()

         expectMostRecentItem().apply {
            items.shouldBeErrorWith(exceptionType = NoNetworkException::class.java, expectedData = createNumbers(0, 10))
            hasAnyDataLeft.shouldBeFalse()
         }

         cancelAndConsumeRemainingEvents()
      }
   }

   @Test
   fun `Update data when database updates`() = runTestWithDispatchers {
      val cachingDataStream = createDataStream()
      cachingDataStream.data.first() // Load data into database

      networkInterceptor.interceptAllFutureCallsWith(
         InterceptionStyle.Error(
            IllegalStateException("Network should not be called now")
         )
      )

      val dataStream = createDataStream()

      dataStream.data.test {
         runCurrent()

         database.value = database.value.copy(items = createNumbers(10, 20))
         runCurrent()

         val numbers = expectMostRecentItem()

         numbers.items shouldBeSuccessWithData createNumbers(10, 20)
      }
   }

   private fun createNumbers(from: Int, to: Int): List<Int> {
      return List(to - from) {
         it + from
      }
   }
}
