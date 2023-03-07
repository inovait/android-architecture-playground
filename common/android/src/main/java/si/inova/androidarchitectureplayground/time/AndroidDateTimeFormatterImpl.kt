package si.inova.androidarchitectureplayground.time

import android.content.Context
import android.text.format.DateFormat
import si.inova.androidarchitectureplayground.common.reporting.ErrorReporter
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.util.Locale
import javax.inject.Inject

/**
 * Provides Android-specific [DateTimeFormatter]s, such as a localized time formatter that respects the user's
 * 12-/24-hour clock preference.
 *
 * Adapted from https://github.com/drewhamilton/AndroidDateTimeFormatters/blob/9dee359a916a6d6e30bfa83613638ece0c3eb688/javatime/src/main/java/dev/drewhamilton/androidtime/format/AndroidDateTimeFormatter.java
 */
class AndroidDateTimeFormatterImpl @Inject constructor(
   private val context: Context,
   private val errorReporter: ErrorReporter
) : BaseAndroidDateTimeFormatter() {

   //endregion
   override fun getSystemTimeSettingAwareShortTimePattern(): String {
      val legacyFormat = DateFormat.getTimeFormat(context)
      return if (legacyFormat is SimpleDateFormat) {
         legacyFormat.toPattern()
      } else {
         errorReporter.report(IllegalStateException("Unknown date format type: $legacyFormat ${legacyFormat.javaClass}"))

         if (DateFormat.is24HourFormat(context)) {
            "HH:mm"
         } else {
            "hh:mm a"
         }
      }
   }

   override fun extractPrimaryLocale(): Locale {
      val configuration = context.resources.configuration
      var locale: Locale? = null
      val localeList = configuration.locales
      if (!localeList.isEmpty) {
         locale = localeList[0]
      }
      if (locale == null) {
         @Suppress("DEPRECATION")
         locale = configuration.locale
      }
      if (locale == null) {
         locale = Locale.getDefault() ?: error("Default locale should never be null")
      }
      return locale
   }
}
