package si.inova.androidarchitectureplayground.home

import android.net.Uri
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import si.inova.androidarchitectureplayground.navigation.conditions.HandleLoginAndReplaceBackstack
import si.inova.androidarchitectureplayground.navigation.keys.HomeScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.ManageProfileScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.PostListScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.UserDetailsScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.UserListScreenKey

class HomeDeepLinksTest {
   private val homeDeepLinks = HomeDeepLinks()

   @Test
   fun `handle posts link`() {
      homeDeepLinks.handleDeepLink(Uri.parse("demoapp://posts"), true) shouldBe
         HandleLoginAndReplaceBackstack(HomeScreenKey, PostListScreenKey)

      homeDeepLinks.handleDeepLink(Uri.parse("demoapp://posts"), false) shouldBe
         HandleLoginAndReplaceBackstack(HomeScreenKey, PostListScreenKey)
   }

   @Test
   fun `handle users link`() {
      homeDeepLinks.handleDeepLink(Uri.parse("demoapp://users"), true) shouldBe
         HandleLoginAndReplaceBackstack(HomeScreenKey, UserListScreenKey)

      homeDeepLinks.handleDeepLink(Uri.parse("demoapp://users"), false) shouldBe
         HandleLoginAndReplaceBackstack(HomeScreenKey, UserListScreenKey)
   }

   @Test
   fun `handle link to specific user`() {
      homeDeepLinks.handleDeepLink(Uri.parse("demoapp://users/2"), true) shouldBe
         HandleLoginAndReplaceBackstack(HomeScreenKey, UserListScreenKey, UserDetailsScreenKey(2))

      homeDeepLinks.handleDeepLink(Uri.parse("demoapp://users/2"), false) shouldBe
         HandleLoginAndReplaceBackstack(HomeScreenKey, UserListScreenKey, UserDetailsScreenKey(2))

      homeDeepLinks.handleDeepLink(Uri.parse("demoapp://users/sdpfgkosdkfio"), false) shouldBe
         HandleLoginAndReplaceBackstack(HomeScreenKey, UserListScreenKey)
   }

   @Test
   fun `handle settings`() {
      homeDeepLinks.handleDeepLink(Uri.parse("demoapp://settings"), true) shouldBe
         HandleLoginAndReplaceBackstack(HomeScreenKey, ManageProfileScreenKey)

      homeDeepLinks.handleDeepLink(Uri.parse("demoapp://settings"), false) shouldBe
         HandleLoginAndReplaceBackstack(HomeScreenKey, ManageProfileScreenKey)
   }

   @Test
   fun `Return null on invalid links`() {
      homeDeepLinks.handleDeepLink(Uri.parse("https://www.google.com"), true).shouldBeNull()
      homeDeepLinks.handleDeepLink(Uri.parse("demoapp://nolink"), true).shouldBeNull()
   }
}
