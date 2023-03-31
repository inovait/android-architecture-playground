package si.inova.androidarchitectureplayground.network.android.util

import android.content.Context
import android.os.Build
import android.os.storage.StorageManager
import androidx.annotation.WorkerThread
import dispatch.core.withDefault
import okhttp3.Cache
import si.inova.androidarchitectureplayground.network.cache.DiskCache
import si.inova.kotlinova.core.reporting.ErrorReporter
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Class that manages creation of global OKHttp's [Cache].
 *
 * Cache size is based on Android's provided [StorageManager.getCacheQuotaBytes] method.
 * That way we can achieve best balance between having big cache and not eating up user's disk space.
 *
 * To create cache, just access [cache] object. Please note that the operations makes some blocking disk accesses and thus, first
 * call to [cache] MUST be done on the worker thread.
 */
@Singleton
class GlobalOkHttpDiskCacheManager @Inject constructor(
   private val context: Context,
   private val errorReporter: ErrorReporter,
) : DiskCache {
   val cache: Cache by lazy {
      if (Thread.currentThread().name == "main") {
         error("Disk cache must not be initialized on the main thread")
      }

      val storageManager = context
         .getSystemService(Context.STORAGE_SERVICE) as StorageManager

      val cacheSize = determineCacheSizeBytes(storageManager)
      createCache(storageManager, cacheSize)
   }

   @WorkerThread
   private fun determineCacheSizeBytes(storageManager: StorageManager): Long {
      return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
         // Older device with unknown cache quota. Just use default size all the time.
         FALLBACK_CACHE_SIZE_BYTES
      } else {
         try {
            val cacheQuota = storageManager.getCacheQuotaBytes(
               storageManager.getUuidForPath(context.cacheDir)
            )

            // Using third of our cache quota for OK HTTP disk cache requests seems reasonable.
            @Suppress("MagicNumber")
            cacheQuota / 3
         } catch (e: Exception) {
            errorReporter.report(e)
            // Cache determining error. Just fallback to default value
            FALLBACK_CACHE_SIZE_BYTES
         }
      }
   }

   private fun createCache(storageManager: StorageManager, cacheSizeBytes: Long): Cache {
      val absoluteCacheFolder = File(context.cacheDir, CACHE_SUBFOLDER)
      absoluteCacheFolder.mkdirs()

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
         storageManager.setCacheBehaviorGroup(absoluteCacheFolder, true)
      }

      return Cache(absoluteCacheFolder, cacheSizeBytes)
   }

   override suspend fun clearForRequest(url: String) {
      withDefault {
         val iterator = cache.urls()
         while (iterator.hasNext()) {
            val checkUrl = iterator.next()
            if (checkUrl.endsWith(url)) {
               iterator.remove()
            }
         }
      }
   }
}

private const val FALLBACK_CACHE_SIZE_BYTES = 20_000_000L // 20 MB
private const val CACHE_SUBFOLDER = "okdisk"
