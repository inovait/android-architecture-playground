package si.inova.androidarchitectureplayground

import android.app.ActivityManager
import android.app.Application
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import android.os.strictmode.Violation
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import coil.Coil
import coil.ImageLoader
import dispatch.core.DefaultDispatcherProvider
import dispatch.core.defaultDispatcher
import si.inova.androidarchitectureplayground.di.ApplicationComponent
import si.inova.androidarchitectureplayground.di.MainApplicationComponent
import si.inova.androidarchitectureplayground.di.create
import si.inova.kotlinova.core.dispatchers.AccessCallbackDispatcherProvider
import si.inova.kotlinova.core.logging.AndroidLogcatLogger
import si.inova.kotlinova.core.logging.LogPriority

open class MyApplication : Application() {
   open val applicationComponent: ApplicationComponent by lazy {
      MainApplicationComponent::class.create(this)
   }

   init {
      if (BuildConfig.DEBUG) {
         // Enable better coroutine stack traces on debug builds
         // this slows down coroutines, so it should not be enabled on release
         // using init instead of onCreate ensures that this is started before any content providers
         System.setProperty("kotlinx.coroutines.debug", "on")
      }
   }

   override fun onCreate() {
      super.onCreate()

      if (!isMainProcess()) {
         // Do not perform any initialisation in other processes, they are usually library-specific
         return
      }

      AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)

      enableStrictMode()

      DefaultDispatcherProvider.set(
         AccessCallbackDispatcherProvider(DefaultDispatcherProvider.get()) {
            if (BuildConfig.DEBUG) {
               error("Dispatchers not provided via coroutine scope.")
            }
         }
      )

      Coil.setImageLoader {
         ImageLoader.Builder(this)
            // Load Coil cache on the background thread
            // See https://github.com/coil-kt/coil/issues/1878
            .interceptorDispatcher(applicationComponent.getDefaultCoroutineScope().defaultDispatcher)
            .build()
      }
   }

   private fun enableStrictMode() {
      // Also check on staging release build, if applicable
      // penaltyListener only supports P and newer, so we are forced to only enable StrictMode on those devices
      if (!BuildConfig.DEBUG || Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
         return
      }

      StrictMode.setVmPolicy(
         VmPolicy.Builder()
            .detectActivityLeaks()
            .detectFileUriExposure()
            .detectContentUriWithoutPermission()
            .run {
               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                  detectImplicitDirectBoot()
                     .detectCredentialProtectedWhileLocked()
               } else {
                  this
               }
            }
            .detectLeakedClosableObjects()
            .detectLeakedRegistrationObjects()
            .detectLeakedSqlLiteObjects()
            .penaltyListener(ContextCompat.getMainExecutor(this@MyApplication)) { e ->
               reportStrictModePenalty(e)
            }
            .build()
      )

      StrictMode.setThreadPolicy(
         StrictMode.ThreadPolicy.Builder()
            .detectCustomSlowCalls()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()
            .detectResourceMismatches()
            .detectUnbufferedIo()
            .penaltyListener(ContextCompat.getMainExecutor(this)) { e ->
               if (BuildConfig.DEBUG) {
                  throw e
               } else {
                  applicationComponent.getErrorReporter().report(e)
               }
            }
            .build()
      )
   }

   private fun reportStrictModePenalty(violation: Violation) {
      val e = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
         violation
      } else {
         IllegalStateException("Strict mode violation: $violation")
      }

      if (
         e.cause == null &&
         (
            STRICT_MODE_EXCLUSIONS.any {
               e.toString().contains(it)
            } ||
               e.stackTrace.any { stackTraceElement ->
                  STRICT_MODE_EXCLUSIONS.any {
                     stackTraceElement.toString().contains(it)
                  }
               }
            )
      ) {
         // Exclude some classes from strict mode, see STRICT_MODE_EXCLUSIONS below.
         return
      }

      if (BuildConfig.DEBUG) {
         throw e
      } else {
         applicationComponent.getErrorReporter().report(e)
      }
   }

   private fun isMainProcess(): Boolean {
      val activityManager = getSystemService<ActivityManager>()!!
      val myPid = android.os.Process.myPid()

      return activityManager.runningAppProcesses?.any {
         it.pid == myPid && packageName == it.processName
      } == true
   }
}

private val STRICT_MODE_EXCLUSIONS = listOf(
   "UnixSecureDirectoryStream", // https://issuetracker.google.com/issues/270704908
   "UnixDirectoryStream", // https://issuetracker.google.com/issues/270704908,
   "SurfaceControl.finalize", // https://issuetracker.google.com/issues/167533582
   "InsetsSourceControl", // https://issuetracker.google.com/issues/307473789
)
