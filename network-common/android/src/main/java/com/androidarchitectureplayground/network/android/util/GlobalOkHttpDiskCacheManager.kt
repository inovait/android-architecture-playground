package com.androidarchitectureplayground.network.android.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.storage.StorageManager
import androidx.annotation.WorkerThread
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.Cache
import si.inova.androidarchitectureplayground.common.reporting.ErrorReporter
import java.io.File
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private const val DEFAULT_CACHE_SIZE = 20_000_000L // 20 MB

private const val CACHE_SUBFOLDER = "okdisk"

private const val PREFERENCES_CACHING = "okdisk"
private const val KEY_CACHE_SIZE = "CacheSize"

/**
 * Class that manages creation of global OKHttp's [Cache].
 *
 * Cache size is based on Android's provided [StorageManager.getCacheQuotaBytes] method.
 * That way we can achieve best balance between having big cache and not eating up user's disk space.
 *
 * Unfortunately, this method can only be called on worker thread (and may take a while to calculate).
 * That is why for the first launch (and on older devices), we use static placeholder cache value of 20MB,
 * launch worker thread to calculate final cache size and save it to disk.
 * On the next app startup, updated cache value will be used instead of placeholder.
 *
 * Since [StorageManager.getCacheQuotaBytes] can change over time, we run the operation
 * on every startup.
 */
@OptIn(DelicateCoroutinesApi::class)
@Singleton
class GlobalOkHttpDiskCacheManager @Inject constructor(
   private val context: Context,
   private val errorReporter: ErrorReporter
) {
   val cache: Cache

   init {
      val preferences = context.getSharedPreferences(PREFERENCES_CACHING, Context.MODE_PRIVATE)
      val storageManager = context
         .getSystemService(Context.STORAGE_SERVICE) as StorageManager

      GlobalScope.launch { determineCacheSize(preferences, storageManager) }
      cache = createCache(preferences, storageManager)
   }

   private fun createCache(preferences: SharedPreferences, storageManager: StorageManager): Cache {
      val cacheSize = preferences.getLong(KEY_CACHE_SIZE, DEFAULT_CACHE_SIZE)

      val absoluteCacheFolder = File(context.cacheDir, CACHE_SUBFOLDER)
      absoluteCacheFolder.mkdirs()

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         storageManager.setCacheBehaviorGroup(absoluteCacheFolder, true)
      }

      return Cache(absoluteCacheFolder, cacheSize)
   }

   @WorkerThread
   private fun determineCacheSize(preferences: SharedPreferences, storageManager: StorageManager) {
      val cacheSize = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
         // Older device with unknown cache quota. Just use default size all the time.
         DEFAULT_CACHE_SIZE
      } else {
         try {
            val cacheQuota = storageManager.getCacheQuotaBytes(
               storageManager.getUuidForPath(context.cacheDir)
            )

            // Using third of our cache quota for OK HTTP disk cache requests seems reasonable.
            @Suppress("MagicNumber")
            cacheQuota / 3
         } catch (e: IOException) {
            errorReporter.report(e)
            // We got calculation error. Just don't save anything, we can retry next time
            return
         }
      }

      preferences.edit().putLong(KEY_CACHE_SIZE, cacheSize).apply()
   }
}
