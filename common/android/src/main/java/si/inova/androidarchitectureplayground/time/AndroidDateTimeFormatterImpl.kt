package si.inova.androidarchitectureplayground.time

import android.content.Context
import android.text.format.DateFormat
import android.util.Log
import java.text.SimpleDateFormat
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.format.FormatStyle
import java.util.Locale
import javax.inject.Inject

/**
 * Provides Android-specific [DateTimeFormatter]s, such as a localized time formatter that respects the user's
 * 12-/24-hour clock preference.
 *
 * Adapted from https://github.com/drewhamilton/AndroidDateTimeFormatters/blob/9dee359a916a6d6e30bfa83613638ece0c3eb688/javatime/src/main/java/dev/drewhamilton/androidtime/format/AndroidDateTimeFormatter.java
 */
class AndroidDateTimeFormatterImpl @Inject constructor(
   private val context: Context
) : AndroidDateTimeFormatter {

   /**
    * Returns a [DateTimeFormatter] that can format the time according to the context's locale.
    * If `timeStyle` is [FormatStyle.SHORT], the formatter also respects the user's 12-/24-hour clock preference.
    *
    * The [FormatStyle.FULL] and [FormatStyle.LONG] styles typically require a time-zone. When formatting
    * using these styles, a [java.time.ZoneId] must be available, either by using [java.time.ZonedDateTime]
    * or [DateTimeFormatter.withZone].
    *
    * @param timeStyle the formatter style to obtain
    * @return the time formatter
    */
   override fun ofLocalizedTime(timeStyle: FormatStyle): DateTimeFormatter {
      val contextPrimaryLocale = extractPrimaryLocale(context)

      // If format is SHORT, try system 12-/24-hour setting-specific time format:
      if (timeStyle == FormatStyle.SHORT) {
         val pattern = getSystemTimeSettingAwareShortTimePattern()
         if (pattern == null) {
            Log.w(TAG, "Couldn't determine time pattern based on system 12-/24-hour setting")
         } else {
            return DateTimeFormatterBuilder()
               .appendPattern(pattern)
               .toFormatter(contextPrimaryLocale) // Match java.time's ofLocalizedTime, which also hard-codes IsoChronology:
               .withChronology(IsoChronology.INSTANCE)
         }
      }
      return DateTimeFormatter.ofLocalizedTime(timeStyle)
         .withLocale(contextPrimaryLocale)
   }
   //endregion
   //region ofLocalizedDate
   /**
    * Returns a locale specific date format for the ISO chronology.
    *
    *
    * This returns a formatter that will format or parse a date. The exact format pattern used varies by locale.
    *
    *
    * The locale is determined from the formatter. The formatter returned directly by this method will use the provided
    * Context's primary locale.
    *
    *
    * Note that the localized pattern is looked up lazily. This `DateTimeFormatter` holds the style required and
    * the locale, looking up the pattern required on demand.
    *
    *
    * The returned formatter has a chronology of ISO set to ensure dates in other calendar systems are correctly
    * converted. It has no override zone and uses the [SMART][java.time.format.ResolverStyle.SMART] resolver
    * style.
    *
    * @param dateStyle the formatter style to obtain
    * @return the date formatter
    */
   override fun ofLocalizedDate(dateStyle: FormatStyle): DateTimeFormatter {
      return DateTimeFormatter.ofLocalizedDate(dateStyle)
         .withLocale(extractPrimaryLocale(context))
   }
   //endregion
   //region ofLocalizedDateTime
   /**
    * Returns a locale specific date-time formatter for the ISO chronology.
    *
    *
    * This returns a formatter that will format or parse a date-time. The exact format pattern used varies by locale.
    *
    *
    * The locale is determined from the formatter. The formatter returned directly by this method will use the provided
    * Context's primary locale.
    *
    *
    * Note that the localized pattern is looked up lazily. This `DateTimeFormatter` holds the style required and
    * the locale, looking up the pattern required on demand.
    *
    *
    * The returned formatter has a chronology of ISO set to ensure dates in other calendar systems are correctly
    * converted. It has no override zone and uses the [SMART][java.time.format.ResolverStyle.SMART] resolver
    * style.
    *
    * @param dateTimeStyle the formatter style to obtain
    * @return the date-time formatter
    */
   override fun ofLocalizedDateTime(dateTimeStyle: FormatStyle): DateTimeFormatter {
      // If format is SHORT, try system 12-/24-hour setting-specific time format:
      if (dateTimeStyle == FormatStyle.SHORT) {
         val systemSpecificFormatter = attemptSystemSettingDateTimeFormatter(dateTimeStyle, dateTimeStyle)
         if (systemSpecificFormatter != null) return systemSpecificFormatter
      }

      // Either the format is not SHORT or we otherwise can't insert the system-specific pattern:
      return DateTimeFormatter.ofLocalizedDateTime(dateTimeStyle)
         .withLocale(extractPrimaryLocale(context))
   }

   /**
    * Returns a locale specific date and time format for the ISO chronology.
    *
    *
    * This returns a formatter that will format or parse a date-time. The exact format pattern used varies by locale.
    *
    *
    * The locale is determined from the formatter. The formatter returned directly by this method will use the provided
    * context's primary locale.
    *
    *
    * Note that the localized pattern is looked up lazily. This `DateTimeFormatter` holds the style required and
    * the locale, looking up the pattern required on demand.
    *
    *
    * The returned formatter has a chronology of ISO set to ensure dates in other calendar systems are correctly
    * converted. It has no override zone and uses the [SMART][java.time.format.ResolverStyle.SMART] resolver
    * style.
    *
    * @param dateStyle the date formatter style to obtain
    * @param timeStyle the time formatter style to obtain
    * @return the date, time or date-time formatter
    */
   override fun ofLocalizedDateTime(
      dateStyle: FormatStyle,
      timeStyle: FormatStyle
   ): DateTimeFormatter {
      // If time format is SHORT, try system 12-/24-hour setting-specific time format:
      if (timeStyle == FormatStyle.SHORT) {
         val systemSpecificFormatter = attemptSystemSettingDateTimeFormatter(dateStyle, timeStyle)
         if (systemSpecificFormatter != null) return systemSpecificFormatter
      }

      // Either the time format is not SHORT or we otherwise can't insert the system-specific pattern:
      return DateTimeFormatter.ofLocalizedDateTime(dateStyle, timeStyle)
         .withLocale(extractPrimaryLocale(context))
   }

   fun attemptSystemSettingDateTimeFormatter(
      dateStyle: FormatStyle,
      timeStyle: FormatStyle
   ): DateTimeFormatter? {
      val timePattern = getSystemTimeSettingAwareShortTimePattern()
      if (timePattern != null) {
         val contextPrimaryLocale = extractPrimaryLocale(context)
         val defaultDateTimePattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(
            dateStyle,
            timeStyle,
            IsoChronology.INSTANCE,
            contextPrimaryLocale
         )
         val defaultTimePattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(
            null,
            timeStyle,
            IsoChronology.INSTANCE,
            contextPrimaryLocale
         )
         if (timePattern != defaultTimePattern && defaultDateTimePattern.contains(defaultTimePattern)) {
            // We can replace the default time pattern with the system-specific one:
            val dateTimePattern = defaultDateTimePattern.replace(defaultTimePattern, timePattern)
            return DateTimeFormatterBuilder()
               .appendPattern(dateTimePattern)
               .toFormatter(contextPrimaryLocale) // Match java.time's ofLocalizedDateTime, which also hard-codes IsoChronology:
               .withChronology(IsoChronology.INSTANCE)
         }
      }
      return null
   }

   //endregion
   fun getSystemTimeSettingAwareShortTimePattern(): String? {
      val legacyFormat = DateFormat.getTimeFormat(context)
      return if (legacyFormat is SimpleDateFormat) {
         legacyFormat.toPattern()
      } else {
         null
      }
   }
   //region ofSkeleton
   /**
    * Returns the best possible localized formatter of the given skeleton for the given context's primary locale. A
    * skeleton is similar to, and uses the same format characters as, a Unicode
    * [UTS #35](http://www.unicode.org/reports/tr35/#Date_Format_Patterns) pattern.
    *
    *
    * One difference is that order is irrelevant. For example, "MMMMd" will become "MMMM d" in the `en_US`
    * locale, but "d. MMMM" in the `de_CH` locale.
    *
    *
    * Note also in that second example that the necessary punctuation for German was added. For the same input in
    * `es_ES`, we'd have even more extra text: "d 'de' MMMM".
    *
    *
    * This method will automatically correct for grammatical necessity. Given the same "MMMMd" input, the formatter
    * will use "d LLLL" in the `fa_IR` locale, where stand-alone months are necessary. **Warning: core
    * library desugaring does not currently support formatting with 'L'.**
    *
    *
    * Lengths are preserved where meaningful, so "Md" would give a different result to "MMMd", say, except in a
    * locale such as `ja_JP` where there is only one length of month.
    *
    *
    * This method will only use patterns that are in CLDR, and is useful whenever you know what elements you want
    * in your format string but don't want to make your code specific to any one locale.
    *
    * @param skeleton a skeleton as described above
    * @return a formatter with the localized pattern based on the skeleton
    */
   override fun ofSkeleton(skeleton: String): DateTimeFormatter {
      return ofSkeleton(skeleton, extractPrimaryLocale(context))
   }

   /**
    * Returns the best possible localized formatter of the given skeleton for the given locale. A skeleton is similar
    * to, and uses the same format characters as, a Unicode
    * [UTS #35](http://www.unicode.org/reports/tr35/#Date_Format_Patterns) pattern.
    *
    *
    * One difference is that order is irrelevant. For example, "MMMMd" will become "MMMM d" in the `en_US`
    * locale, but "d. MMMM" in the `de_CH` locale.
    *
    *
    * Note also in that second example that the necessary punctuation for German was added. For the same input in
    * `es_ES`, we'd have even more extra text: "d 'de' MMMM".
    *
    *
    * This method will automatically correct for grammatical necessity. Given the same "MMMMd" input, the formatter
    * will use "d LLLL" in the `fa_IR` locale, where stand-alone months are necessary. **Warning: core
    * library desugaring does not currently support formatting with 'L'.**
    *
    *
    * Lengths are preserved where meaningful, so "Md" would give a different result to "MMMd", say, except in a
    * locale such as `ja_JP` where there is only one length of month.
    *
    *
    * This method will only use patterns that are in CLDR, and is useful whenever you know what elements you want
    * in your format string but don't want to make your code specific to any one locale.
    *
    * @param skeleton a skeleton as described above
    * @param locale the locale into which the skeleton should be localized
    * @return a formatter with the localized pattern based on the skeleton
    */
   override fun ofSkeleton(skeleton: String, locale: Locale): DateTimeFormatter {
      val pattern = DateFormat.getBestDateTimePattern(locale, skeleton)
      return DateTimeFormatter.ofPattern(pattern, locale)
   }

   //endregion
   private fun extractPrimaryLocale(context: Context): Locale {
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

private val TAG = AndroidDateTimeFormatterImpl::class.java.simpleName
