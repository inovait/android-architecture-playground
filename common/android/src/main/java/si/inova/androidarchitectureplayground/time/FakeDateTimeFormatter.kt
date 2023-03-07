package si.inova.androidarchitectureplayground.time

import java.util.Locale

/**
 * Variant of the DateTimeFormatter that uses predefined config. Use for tests and Compose preview
 */
class FakeDateTimeFormatter(
   private val locale: Locale = Locale("US"),
   private val use24hTime: Boolean = true,
) : BaseAndroidDateTimeFormatter() {

   //endregion
   override fun getSystemTimeSettingAwareShortTimePattern(): String {
      return if (use24hTime) {
         "HH:mm"
      } else {
         "hh:mm a"
      }
   }

   override fun extractPrimaryLocale(): Locale {
      return locale
   }
}
