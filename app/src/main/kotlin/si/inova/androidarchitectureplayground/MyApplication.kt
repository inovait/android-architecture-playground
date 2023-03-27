package si.inova.androidarchitectureplayground

import android.app.Application
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.core.content.ContextCompat
import com.deliveryhero.whetstone.Whetstone
import com.deliveryhero.whetstone.app.ApplicationComponent
import com.deliveryhero.whetstone.app.ApplicationComponentOwner
import com.deliveryhero.whetstone.app.ContributesAppInjector
import si.inova.androidarchitectureplayground.common.logging.LogPriority
import si.inova.androidarchitectureplayground.common.reporting.ErrorReporter
import si.inova.androidarchitectureplayground.di.DaggerMyApplicationComponent
import si.inova.androidarchitectureplayground.logging.AndroidLogcatLogger
import javax.inject.Inject
import javax.inject.Provider

@ContributesAppInjector(generateAppComponent = false)
open class MyApplication : Application(), ApplicationComponentOwner {
   @Inject
   lateinit var errorReporter: Provider<ErrorReporter>

   override fun onCreate() {
      Whetstone.inject(this)

      super.onCreate()

      AndroidLogcatLogger.installOnDebuggableApp(this, minPriority = LogPriority.VERBOSE)

      enableStrictMode()
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
               if (
                  e.cause == null &&
                  e.stackTrace.any { it.className.contains("UnixSecureDirectoryStream") }
               ) {
                  // workaround for the https://issuetracker.google.com/issues/270704908
                  return@penaltyListener
               }

               if (BuildConfig.DEBUG) {
                  throw e
               } else {
                  errorReporter.get().report(e)
               }
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
                  errorReporter.get().report(e)
               }
            }
            .build()
      )
   }

   override val applicationComponent: ApplicationComponent by lazy {
      DaggerMyApplicationComponent.factory().create(this)
   }
}
