package si.inova.androidarchitectureplayground.home

import android.net.Uri
import me.tatarka.inject.annotations.Inject
import si.inova.androidarchitectureplayground.navigation.conditions.ReplaceBackstackOrOpenScreenWithLogin
import si.inova.androidarchitectureplayground.navigation.keys.HomeScreenKey
import si.inova.kotlinova.navigation.deeplink.DeepLinkHandler
import si.inova.kotlinova.navigation.deeplink.handleMultipleDeepLinks
import si.inova.kotlinova.navigation.di.OuterNavigationScope
import si.inova.kotlinova.navigation.instructions.NavigationInstruction
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding

@ContributesBinding(OuterNavigationScope::class, multibinding = true)
class HomeDeepLinks @Inject constructor() : DeepLinkHandler {
   override fun handleDeepLink(uri: Uri, startup: Boolean): NavigationInstruction? {
      return handleMultipleDeepLinks(uri, startup) {
         matchDeepLink("demoapp://posts") { _, _ ->
            ReplaceBackstackOrOpenScreenWithLogin(startup, HomeScreenKey(HomeScreenKey.Tab.POSTS))
         }
         matchDeepLink("demoapp://users") { _, _ ->
            ReplaceBackstackOrOpenScreenWithLogin(startup, HomeScreenKey(HomeScreenKey.Tab.USERS))
         }
         matchDeepLink("demoapp://users/{user}") { args, _ ->
            ReplaceBackstackOrOpenScreenWithLogin(
               startup,
               HomeScreenKey(HomeScreenKey.Tab.USERS, userDetailsId = args.getValue("user"))
            )
         }
         matchDeepLink("demoapp://settings") { _, _ ->
            ReplaceBackstackOrOpenScreenWithLogin(startup, HomeScreenKey(HomeScreenKey.Tab.SETTINGS))
         }
      }
   }
}
