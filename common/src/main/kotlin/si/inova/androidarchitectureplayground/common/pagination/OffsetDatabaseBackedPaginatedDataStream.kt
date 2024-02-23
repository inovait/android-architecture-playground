package si.inova.androidarchitectureplayground.common.pagination

import dispatch.core.withIO
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import si.inova.kotlinova.core.outcome.LoadingStyle
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.core.outcome.mapData
import si.inova.kotlinova.core.outcome.mapNullableData

/**
 * A [PaginatedDataStream] that loads individual pages based on the item offset, with database used for caching.
 *
 * @param pageSize number of items to load per page
 * @param loadFromNetwork function that loads data from the network.
 *                        It should return [Outcome.Error] if the network request failed.
 * @param loadFromDatabase function that loads data from the database. Resulting data class should specify
 *                        [DatabaseResult.isCacheStillValid] to indicate whether the data in the database is still valid. When
 *                        set to false, the data will be reloaded from the network.
 * @param saveToDatabase function that saves data to the database. It should replace existing data if [replaceExisting] is true,
 *                       otherwise it should just append to the existing data.
 */
class OffsetDatabaseBackedPaginatedDataStream<T>(
   private val pageSize: Int = DEFAULT_PAGE_SIZE,
   private val loadFromNetwork: suspend (offset: Int, limit: Int) -> Outcome<List<T>>,
   private val loadFromDatabase: (offset: Int, limit: Int) -> Flow<Outcome<DatabaseResult<T>>>,
   private val saveToDatabase: suspend (data: List<T>, replaceExisting: Boolean) -> Unit,
) : FlowSwitchingPaginatedDataStream<List<T>>() {
   override suspend fun load(): Unit = withIO {
      if (!loadPage(0)) {
         return@withIO
      }

      var page = 1
      var anyDataLeft = true
      while (anyDataLeft && currentCoroutineContext().isActive) {
         nextPageChannel.receive()
         anyDataLeft = loadPage(page++)
      }

      awaitCancellation()
   }

   private suspend fun loadPage(page: Int): Boolean {
      val offset = page * pageSize
      val totalItems = (page + 1) * pageSize

      val dbData = loadFromDatabase(0, totalItems).first().data
         ?: DatabaseResult(emptyList(), false)

      if (dbData.items.size >= totalItems && dbData.isCacheStillValid) {
         feedFlowFromDatabase(offset = 0, limit = totalItems, hasAnyDataLeft = true)
      } else {
         val loadingStyle = if (page == 0) {
            LoadingStyle.NORMAL
         } else {
            LoadingStyle.ADDITIONAL_DATA
         }

         targetFlow.value = flowOf(
            PaginatedDataStream.PaginationResult(
               Outcome.Progress(dbData.items, style = loadingStyle),
               true
            )
         )

         val networkOutcome = loadFromNetwork(offset, pageSize)
         when (networkOutcome) {
            is Outcome.Error -> {
               val networkOutcomeWithData = networkOutcome.mapNullableData { dbData.items }
               targetFlow.value = flowOf(PaginatedDataStream.PaginationResult(networkOutcomeWithData, false))
               return false
            }

            is Outcome.Progress -> error("loadFromNetwork should not return progress. Got $networkOutcome")
            is Outcome.Success -> {
               val networkItems = networkOutcome.data
               if (networkItems.isEmpty()) {
                  feedFlowFromDatabase(offset = 0, limit = totalItems, hasAnyDataLeft = false)
                  return false
               }

               saveToDatabase(networkItems, page == 0)

               val hasAnyDataLeft = networkItems.size >= pageSize
               feedFlowFromDatabase(offset = 0, limit = totalItems, hasAnyDataLeft = hasAnyDataLeft)
               return hasAnyDataLeft
            }
         }
      }

      return true
   }

   private fun feedFlowFromDatabase(offset: Int, limit: Int, hasAnyDataLeft: Boolean) {
      targetFlow.value =
         loadFromDatabase(offset, limit)
            .map { dbResult ->
               PaginatedDataStream.PaginationResult(dbResult.mapData { it.items }, hasAnyDataLeft)
            }
   }

   data class DatabaseResult<T>(val items: List<T>, val isCacheStillValid: Boolean)
}

private const val DEFAULT_PAGE_SIZE = 10
