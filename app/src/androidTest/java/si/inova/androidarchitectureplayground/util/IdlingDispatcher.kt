package si.inova.androidarchitectureplayground.util

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Runnable
import kotlin.coroutines.CoroutineContext

/**
 * [IdlingResource] helper for coroutines.  This class simply wraps a delegate [CoroutineDispatcher]
 * and keeps a running count of all coroutines it creates, decrementing the count when they complete.
 *
 * @param delegate The [CoroutineDispatcher] which will be used for actual dispatch.
 * @see IdlingResource
 * @see CoroutineDispatcher
 *
 * A copy of the [dispatch.android.espresso.IdlingDispatcher] with https://github.com/RBusarow/Dispatch/pull/619 fix applied.
 */
class IdlingDispatcher(
   val delegate: CoroutineDispatcher
) : CoroutineDispatcher() {

   /**
    * The [CountingIdlingResource] which is responsible for Espresso functionality.
    */
   val counter: CountingIdlingResource = CountingIdlingResource("IdlingResource for $this")

   /**
    * @return
    * * true if the [counter]'s count is zero
    * * false if the [counter]'s count is non-zero
    */
   fun isIdle(): Boolean = counter.isIdleNow

   /**
    * Counting implementation of the [dispatch][CoroutineDispatcher.dispatch] function.
    *
    * The count is incremented for every dispatch, and decremented for every completion, including suspension.
    */
   override fun dispatch(context: CoroutineContext, block: Runnable) {
      val runnable = Runnable {
         counter.increment()
         try {
            block.run()
         } finally {
            counter.decrement()
         }
      }
      delegate.dispatch(context, runnable)
   }

   /**
    * @suppress
    */
   override fun toString(): String = "CountingDispatcher delegating to $delegate"
}
