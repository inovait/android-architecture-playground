package si.inova.androidarchitectureplayground.home

import android.net.Uri
import com.squareup.anvil.annotations.ContributesMultibinding
import si.inova.androidarchitectureplayground.navigation.conditions.ReplaceBackstackOrOpenScreenWithLogin
import si.inova.androidarchitectureplayground.navigation.keys.HomeScreenKey
import si.inova.kotlinova.navigation.deeplink.DeepLinkHandler
import si.inova.kotlinova.navigation.deeplink.handleMultipleDeepLinks
import si.inova.kotlinova.navigation.di.OuterNavigationScope
import si.inova.kotlinova.navigation.instructions.NavigationInstruction
import javax.inject.Inject

@ContributesMultibinding(OuterNavigationScope::class)
class HomeDeepLinks @Inject constructor() : DeepLinkHandler {
   override fun handleDeepLink(uri: Uri, startup: Boolean): NavigationInstruction? {
      return handleMultipleDeepLinks(uri, startup) {
         matchDeepLink("demoapp://posts") { _, _ ->
            ReplaceBackstackOrOpenScreenWithLogin(startup, HomeScreenKey(HomeScreenKey.Tab.POSTS))
         }
         matchDeepLink("demoapp://users") { _, _ ->
            ReplaceBackstackOrOpenScreenWithLogin(startup, HomeScreenKey(HomeScreenKey.Tab.USERS))
         }
         matchDeepLink("demoapp://settings") { _, _ ->
            ReplaceBackstackOrOpenScreenWithLogin(startup, HomeScreenKey(HomeScreenKey.Tab.SETTINGS))
         }
      }
   }
}
