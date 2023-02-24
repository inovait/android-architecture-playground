package si.inova.androidarchitectureplayground.screens

import android.net.Uri
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesMultibinding
import si.inova.androidarchitectureplayground.navigation.base.DeepLinkHandler
import si.inova.androidarchitectureplayground.navigation.base.handleMultipleDeepLinks
import si.inova.androidarchitectureplayground.navigation.instructions.ReplaceHistory
import si.inova.androidarchitectureplayground.navigation.keys.ScreenAKey
import si.inova.androidarchitectureplayground.navigation.keys.ScreenBKey
import javax.inject.Inject

@ContributesMultibinding(ApplicationScope::class)
class ScreenCDeepLinkHandler @Inject constructor() : DeepLinkHandler {
   override fun handleDeepLink(uri: Uri) = handleMultipleDeepLinks(uri) {
      matchDeepLink(
         "inova://test/d/{number}?key={value}",
         "inova://test/other?key={value}&number={number}"
      ) {
         val id = it["number"]?.toIntOrNull() ?: return@matchDeepLink null
         val key = it["value"] ?: return@matchDeepLink null

         ReplaceHistory(
            ScreenAKey,
            ScreenBKey,
            ScreenCKey(id, key)
         )
      }
   }
}
