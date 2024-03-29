package si.inova.androidarchitectureplayground.instrumentation

import android.app.Application
import android.app.Instrumentation
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner

@Suppress("unused")
class TestRunner : AndroidJUnitRunner() {
   override fun newApplication(
      cl: ClassLoader,
      className: String,
      context: Context,
   ): Application = Instrumentation.newApplication(TestApplication::class.java, context)
}
