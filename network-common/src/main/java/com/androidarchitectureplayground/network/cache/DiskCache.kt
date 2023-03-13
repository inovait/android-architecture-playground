package com.androidarchitectureplayground.network.cache

fun interface DiskCache {
   /**
    * Clear all disk cache entries for the given URL
    */
   suspend fun clearForRequest(url: String)
}
