package si.inova.androidarchitectureplayground.user.ui.list

import androidx.annotation.IntRange
import androidx.annotation.MainThread
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingDataEvent
import androidx.paging.PagingDataPresenter
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import androidx.paging.liveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import si.inova.androidarchitectureplayground.common.flow.AwayDetectorFlow
import si.inova.androidarchitectureplayground.common.logging.ActionLogger
import si.inova.androidarchitectureplayground.user.UserRepository
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.kotlinova.core.exceptions.UnknownCauseException
import si.inova.kotlinova.core.logging.logcat
import si.inova.kotlinova.core.outcome.CauseException
import si.inova.kotlinova.core.outcome.CoroutineResourceManager
import si.inova.kotlinova.core.outcome.LoadingStyle
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.services.CoroutineScopedService
import si.inova.kotlinova.navigation.services.InjectScopedService

@InjectScopedService
class UserListViewModel @Inject constructor(
   private val resources: CoroutineResourceManager,
   private val userRepository: UserRepository,
   private val actionLogger: ActionLogger,
) : CoroutineScopedService(resources.scope) {
   private val _userList = MutableStateFlow<Outcome<PagedList<User>>>(Outcome.Progress())
   val userList: StateFlow<Outcome<PagedList<User>>>
      get() = _userList

   private var pagingResult: PagingResult<User>? = null

   override fun onServiceRegistered() {
      actionLogger.logAction { "UserListViewModel.onServiceRegistered()" }
      loadUserList()
   }

   private fun loadUserList(force: Boolean = false) = resources.launchResourceControlTask(_userList) {
      val pagingResult = Pager(PagingConfig(pageSize = 10), pagingSourceFactory = { userRepository.getAllUsers() })
         .flow
         .unwrap(this)

      this@UserListViewModel.pagingResult = pagingResult

      println("emit all paging user list")
      emitAll(pagingResult.data)
   }

   fun refresh() {
      actionLogger.logAction { "UserListViewModel.refresh()" }
      pagingResult?.refresh()
   }
}

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

fun <T : Any> Flow<PagingData<T>>.unwrap(coroutineScope: CoroutineScope): PagingResult<T> {
   val cached = cachedIn(coroutineScope) as SharedFlow<PagingData<T>>

   val pagingListFlow = MutableStateFlow<PagedList<T>?>(null)

   val presenter = object : PagingDataPresenter<T>(cachedPagingData = cached.replayCache.firstOrNull()) {
      override suspend fun presentPagingDataEvent(event: PagingDataEvent<T>) {
         try {
            println("present event ${event.javaClass.simpleName} ${size}")
            pagingListFlow.value = PagedListImpl(this)
         } finally {
            println("finally")
         }
      }
   }

   pagingListFlow.value = PagedListImpl(presenter)

   val dataFlow: Flow<Outcome<PagedList<T>>> = flow {
      coroutineScope {
         launch {
            this@unwrap.onEach { println("pending paging data $it") }.collectLatest {
               println("got paging data $it")
               presenter.collectFrom(it)
            }
         }

         emitAll(
            presenter.loadStateFlow.onStart { logcat { "start collecting loadState" } }
               .onCompletion { logcat { "stop collecting flow strate" } }.flatMapLatest { loadStates ->
               println("got loadStates $loadStates")
               pagingListFlow.filterNotNull().map {
                  println("got data ${it.size}")
                  if (loadStates == null) {
                     return@map Outcome.Progress(it)
                  }

                  val append = loadStates.append
                  val prepend = loadStates.prepend
                  val refresh = loadStates.refresh

                  if (append is LoadState.Loading) {
                     Outcome.Progress(it, style = LoadingStyle.ADDITIONAL_DATA)
                  } else if (refresh is LoadState.Loading) {
                     Outcome.Progress(it, style = LoadingStyle.USER_REQUESTED_REFRESH)
                  } else if (prepend is LoadState.Loading) {
                     Outcome.Progress(it)
                  } else if (append is LoadState.Error) {
                     Outcome.Error(append.error.toCauseException(), data = it)
                  } else if (refresh is LoadState.Error) {
                     Outcome.Error(refresh.error.toCauseException(), data = it)
                  } else if (prepend is LoadState.Error) {
                     Outcome.Error(prepend.error.toCauseException(), data = it)
                  } else {
                     Outcome.Success(it)
                  }
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

interface PagedList<T> {
   /**
    * @return Total number of presented items, including placeholders.
    */
   val size: Int

   /**
    * Returns the presented item at the specified position, notifying Paging of the item access to
    * trigger any loads necessary to fulfill [prefetchDistance][PagingConfig.prefetchDistance].
    *
    * @param index Index of the presented item to return, including placeholders.
    * @return The presented item at position [index], `null` if it is a placeholder.
    */
   @MainThread
   public operator fun get(
      @IntRange(from = 0)
      index: Int,
   ): T?

   /**
    * Returns the presented item at the specified position, without notifying Paging of the item
    * access that would normally trigger page loads.
    *
    * @param index Index of the presented item to return, including placeholders.
    * @return The presented item at position [index], `null` if it is a placeholder
    */
   @MainThread
   public fun peek(
      @IntRange(from = 0)
      index: Int,
   ): T?

   // Force overriding of equals to conform to Compose stability
   override fun hashCode(): Int
   override fun equals(other: Any?): Boolean
}

private class PagedListImpl<T : Any>(
   private val presenter: PagingDataPresenter<T>,
   private val snapshot: List<T?> = presenter.snapshot(),
) : PagedList<T> {

   override val size: Int
      get() = snapshot.size

   override fun get(index: Int): T? {
      presenter[index]
      return snapshot.get(index)
   }

   override fun peek(index: Int): T? {
      return snapshot.get(index)
   }

   override fun hashCode(): Int {
      return snapshot.hashCode()
   }

   override fun equals(other: Any?): Boolean {
      return if (other is PagedListImpl<*>) {
         snapshot.equals(other.snapshot)
      } else {
         false
      }
   }
}

fun <T> emptyPagedList(): PagedList<T> {
   return object : PagedList<T> {
      override val size: Int
         get() = 0

      override fun get(index: Int): T? {
         throw IndexOutOfBoundsException("Index ${index}out of bounds")
      }

      override fun peek(index: Int): T? {
         throw IndexOutOfBoundsException("Index ${index}out of bounds")
      }

      override fun hashCode(): Int {
         return 0
      }

      override fun equals(other: Any?): Boolean {
         return if (other is PagedListImpl<*>) {
            other.size == 0
         } else {
            false
         }
      }
   }
}

private fun Throwable.toCauseException(): CauseException {
   return if (this is CauseException) {
      this
   } else {
      UnknownCauseException(cause = this)
   }
}
