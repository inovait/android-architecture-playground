package si.inova.androidarchitectureplayground.post.ui.post

import android.net.Uri
import com.squareup.anvil.annotations.ContributesMultibinding
import si.inova.androidarchitectureplayground.common.di.ApplicationScope
import si.inova.androidarchitectureplayground.navigation.keys.HomeScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.PostScreenKey
import si.inova.kotlinova.navigation.deeplink.DeepLinkHandler
import si.inova.kotlinova.navigation.deeplink.matchDeepLink
import si.inova.kotlinova.navigation.instructions.NavigationInstruction
import si.inova.kotlinova.navigation.instructions.ReplaceBackstackOrOpenScreen
import javax.inject.Inject

@ContributesMultibinding(ApplicationScope::class)
class PostScreenDeepLinkHandler @Inject constructor() : DeepLinkHandler {
   override fun handleDeepLink(uri: Uri, startup: Boolean): NavigationInstruction? {
      return uri.matchDeepLink("demoapp://posts/{id}") { args ->
         val id = args.getValue("id").toIntOrNull() ?: return null

         val targetKey = PostScreenKey(id)

         return ReplaceBackstackOrOpenScreen(startup, HomeScreenKey(selectedTab = HomeScreenKey.Tab.POSTS), targetKey)
      }
   }
}
