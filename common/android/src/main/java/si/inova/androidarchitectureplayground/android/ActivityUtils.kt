package si.inova.androidarchitectureplayground.android

import android.content.Context
import android.content.ContextWrapper
import androidx.fragment.app.FragmentActivity

fun Context.findActivity(): FragmentActivity? {
   var currentContext: Context? = this

   while (currentContext != null) {
      when (currentContext) {
         is FragmentActivity -> {
            return currentContext
         }

         is ContextWrapper -> {
            currentContext = currentContext.baseContext
         }

         else -> {
            return null
         }
      }
   }

   return null
}

fun Context.requireActivity(): FragmentActivity {
   return findActivity() ?: error("$this is not an activity context")
}
