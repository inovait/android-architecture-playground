package si.inova.androidarchitectureplayground.paging

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import si.inova.kotlinova.core.outcome.Outcome

/**
 * Transformation of the [PagingData] into a standard Flow that can then be collected, combined, mapped etc. with other flows and
 * then used normally in Compose screens, without having to use any paging-specific code in the UI layer.
 */
interface PagingResult<T> {
   val data: Flow<Outcome<PagedList<T>>>

   /**
    * Refresh the data presented by this [PagingDataPresenter].
    */
   fun refresh()

   /**
    * Retry any failed load requests that are part of this PagingResult.
    *
    * Unlike [refresh], this does not invalidate [PagingSource], it only retries failed loads
    * within the same generation of [PagingData].
    *
    **/
   fun retry()
}
