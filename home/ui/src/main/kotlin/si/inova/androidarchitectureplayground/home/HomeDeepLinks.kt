package si.inova.androidarchitectureplayground.home

import android.net.Uri
import dev.zacsweers.metro.ContributesIntoSet
import dev.zacsweers.metro.Inject
import si.inova.androidarchitectureplayground.navigation.conditions.HandleLoginAndReplaceBackstack
import si.inova.androidarchitectureplayground.navigation.keys.HomeScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.ManageProfileScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.PostDetailsScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.PostListScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.UserDetailsScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.UserListScreenKey
import si.inova.kotlinova.navigation.deeplink.DeepLinkHandler
import si.inova.kotlinova.navigation.deeplink.handleMultipleDeepLinks
import si.inova.kotlinova.navigation.di.OuterNavigationScope
import si.inova.kotlinova.navigation.instructions.NavigationInstruction

@ContributesIntoSet(OuterNavigationScope::class)
@Inject
class HomeDeepLinks : DeepLinkHandler {
   override fun handleDeepLink(uri: Uri, startup: Boolean): NavigationInstruction? {
      return handleMultipleDeepLinks(uri, startup) {
         matchDeepLink("demoapp://posts") { _, _ ->
            HandleLoginAndReplaceBackstack(HomeScreenKey, PostListScreenKey)
         }
         matchDeepLink("demoapp://users") { _, _ ->
            HandleLoginAndReplaceBackstack(HomeScreenKey, UserListScreenKey)
         }
         matchDeepLink("demoapp://users/{user}") { args, _ ->
            val userId = args.getValue("user").toIntOrNull()

            if (userId != null) {
               HandleLoginAndReplaceBackstack(
                  HomeScreenKey,
                  UserListScreenKey,
                  UserDetailsScreenKey(userId)
               )
            } else {
               HandleLoginAndReplaceBackstack(
                  HomeScreenKey,
                  UserListScreenKey,
               )
            }
         }
         matchDeepLink("demoapp://posts/{post}") { args, _ ->
            val postId = args.getValue("post").toIntOrNull()

            if (postId != null) {
               HandleLoginAndReplaceBackstack(
                  HomeScreenKey,
                  PostListScreenKey,
                  PostDetailsScreenKey(postId)
               )
            } else {
               HandleLoginAndReplaceBackstack(
                  HomeScreenKey,
                  PostListScreenKey,
               )
            }
         }
         matchDeepLink("demoapp://settings") { _, _ ->
            HandleLoginAndReplaceBackstack(HomeScreenKey, ManageProfileScreenKey)
         }
      }
   }
}
