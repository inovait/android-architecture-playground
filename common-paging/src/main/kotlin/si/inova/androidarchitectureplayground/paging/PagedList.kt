package si.inova.androidarchitectureplayground.paging

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
   operator fun get(
      index: Int,
   ): T?

   /**
    * Returns the presented item at the specified position, without notifying Paging of the item
    * access that would normally trigger page loads.
    *
    * @param index Index of the presented item to return, including placeholders.
    * @return The presented item at position [index], `null` if it is a placeholder
    */
   fun peek(
      index: Int,
   ): T?

   // Force overriding of equals to conform to Compose stability
   override fun hashCode(): Int
   override fun equals(other: Any?): Boolean
}

open class ListBackedPagedListImpl<T : Any>(
   private val snapshot: List<T?>,
) : PagedList<T> {

   override val size: Int
      get() = snapshot.size

   open override fun get(index: Int): T? {
      return snapshot[index]
   }

   override fun peek(index: Int): T? {
      return snapshot[index]
   }

   override fun hashCode(): Int {
      return snapshot.hashCode()
   }

   override fun equals(other: Any?): Boolean {
      return if (other is ListBackedPagedListImpl<*>) {
         snapshot.equals(other.snapshot)
      } else {
         false
      }
   }

   override fun toString(): String {
      return snapshot.toString()
   }
}

fun <T : Any> pagedListOf(items: List<T> = emptyList()): PagedList<T> {
   return ListBackedPagedListImpl(items)
}
