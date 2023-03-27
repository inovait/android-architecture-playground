package si.inova.androidarchitectureplayground.screens

import android.net.Uri
import com.deliveryhero.whetstone.app.ApplicationScope
import com.squareup.anvil.annotations.ContributesMultibinding
import si.inova.androidarchitectureplayground.navigation.base.ClearBackstackAnd
import si.inova.androidarchitectureplayground.navigation.base.DeepLinkHandler
import si.inova.androidarchitectureplayground.navigation.base.handleMultipleDeepLinks
import si.inova.androidarchitectureplayground.navigation.conditions.UserLoggedIn
import si.inova.androidarchitectureplayground.navigation.instructions.NavigateWithConditions
import si.inova.androidarchitectureplayground.navigation.instructions.ReplaceHistory
import si.inova.androidarchitectureplayground.navigation.keys.ScreenAKey
import si.inova.androidarchitectureplayground.navigation.keys.ScreenBKey
import javax.inject.Inject

@ContributesMultibinding(ApplicationScope::class)
class ScreenCDeepLinkHandler @Inject constructor() : DeepLinkHandler {
   override fun handleDeepLink(uri: Uri, startup: Boolean) = handleMultipleDeepLinks(uri, startup) {
      matchDeepLink(
         "inova://test/d/{number}?key={value}",
         "inova://test/other?key={value}&number={number}"
      ) { it, startup ->
         val id = it["number"]?.toIntOrNull() ?: return@matchDeepLink null
         val key = it["value"] ?: return@matchDeepLink null

         NavigateWithConditions(
            ReplaceHistory(
               ScreenAKey,
               ScreenBKey(),
               ScreenCKey(id, key)
            ),
            UserLoggedIn,
            conditionScreenWrapper = {
               if (startup) {
                  ClearBackstackAnd(it)
               } else {
                  it
               }
            }
         )
      }
   }
}
