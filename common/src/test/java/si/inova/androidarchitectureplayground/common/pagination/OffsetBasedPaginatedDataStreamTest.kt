package si.inova.androidarchitectureplayground.common.pagination

import io.kotest.matchers.shouldBe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import si.inova.androidarchitectureplayground.common.outcome.LoadingStyle
import si.inova.androidarchitectureplayground.common.outcome.Outcome
import si.inova.androidarchitectureplayground.test.util.testWithExceptions

internal class OffsetBasedPaginatedDataStreamTest {
   private var fakeResponses = ArrayDeque<List<Outcome<List<Int>>>>()
   private val receivedOffsets = ArrayList<Int>()

   @Test
   internal fun `Load first page`() = runTest {
      fakeResponses += listOf(
         Outcome.Progress(listOf(1, 2)),
         Outcome.Success(listOf(2, 3))
      )

      val paginatedStream = OffsetBasedPaginatedDataStream(::createFakeDataSource)

      paginatedStream.data.testWithExceptions {
         awaitItem() shouldBe PaginatedDataStream.PaginationResult(Outcome.Progress(listOf(1, 2)), true)
         awaitItem() shouldBe PaginatedDataStream.PaginationResult(Outcome.Success(listOf(2, 3)), true)

         runCurrent()
         expectNoEvents()
      }
   }

   @Test
   internal fun `Load second page with merged data of both`() = runTest {
      fakeResponses += listOf(
         Outcome.Success(listOf(1, 2))
      )

      fakeResponses += listOf(
         Outcome.Success(listOf(3, 4))
      )

      val paginatedStream = OffsetBasedPaginatedDataStream(::createFakeDataSource)

      paginatedStream.data.testWithExceptions {
         awaitItem() shouldBe PaginatedDataStream.PaginationResult(Outcome.Success(listOf(1, 2)), true)

         paginatedStream.nextPage()
         awaitItem() shouldBe PaginatedDataStream.PaginationResult(
            Outcome.Progress(
               listOf(1, 2),
               style = LoadingStyle.ADDITIONAL_DATA
            ), true
         )
         awaitItem() shouldBe PaginatedDataStream.PaginationResult(Outcome.Success(listOf(1, 2, 3, 4)), true)

         runCurrent()
         expectNoEvents()
      }
   }

   @Test
   internal fun `Pass all loading of subsequent pages as ADDITIONAL_DATA loading style`() = runTest {
      fakeResponses += listOf(
         Outcome.Success(listOf(1, 2))
      )

      fakeResponses += listOf(
         Outcome.Progress(listOf(3, 4)),
         Outcome.Success(listOf(3, 4, 5))
      )

      val paginatedStream = OffsetBasedPaginatedDataStream(::createFakeDataSource)

      paginatedStream.data.testWithExceptions {
         awaitItem() shouldBe PaginatedDataStream.PaginationResult(Outcome.Success(listOf(1, 2)), true)

         paginatedStream.nextPage()
         awaitItem() shouldBe PaginatedDataStream.PaginationResult(
            Outcome.Progress(
               listOf(1, 2),
               style = LoadingStyle.ADDITIONAL_DATA
            ), true
         )
         awaitItem() shouldBe PaginatedDataStream.PaginationResult(
            Outcome.Progress(
               listOf(1, 2, 3, 4),
               style = LoadingStyle.ADDITIONAL_DATA
            ), true
         )
         awaitItem() shouldBe PaginatedDataStream.PaginationResult(Outcome.Success(listOf(1, 2, 3, 4, 5)), true)

         runCurrent()
         expectNoEvents()
      }
   }

   @Test
   internal fun `Set has any data left to false when receiving empty response and ignore subsequent next page calls`() = runTest {
      fakeResponses += listOf(
         Outcome.Success(listOf(1, 2))
      )

      fakeResponses += listOf(
         Outcome.Success(listOf(3, 4))
      )

      fakeResponses += listOf(
         Outcome.Success(emptyList())
      )

      val paginatedStream = OffsetBasedPaginatedDataStream(::createFakeDataSource)

      paginatedStream.data.testWithExceptions {
         awaitItem() shouldBe PaginatedDataStream.PaginationResult(Outcome.Success(listOf(1, 2)), true)

         paginatedStream.nextPage()
         awaitItem() shouldBe PaginatedDataStream.PaginationResult(
            Outcome.Progress(
               listOf(1, 2),
               style = LoadingStyle.ADDITIONAL_DATA
            ), true
         )
         awaitItem() shouldBe PaginatedDataStream.PaginationResult(Outcome.Success(listOf(1, 2, 3, 4)), true)

         paginatedStream.nextPage()
         awaitItem() shouldBe PaginatedDataStream.PaginationResult(
            Outcome.Progress(
               listOf(1, 2, 3, 4),
               style = LoadingStyle.ADDITIONAL_DATA
            ), true
         )
         awaitItem() shouldBe PaginatedDataStream.PaginationResult(Outcome.Success(listOf(1, 2, 3, 4)), false)

         paginatedStream.nextPage()
         paginatedStream.nextPage()
         paginatedStream.nextPage()
         paginatedStream.nextPage()

         runCurrent()
         expectNoEvents()
      }
   }

   private fun createFakeDataSource(offset: Int): Flow<Outcome<List<Int>>> {
      receivedOffsets += offset

      val next = fakeResponses.removeFirstOrNull()
         ?: error("No more mocked responses. Was nextPage called too many times? Called with offsets: $receivedOffsets")

      return next.asFlow()
   }
}
