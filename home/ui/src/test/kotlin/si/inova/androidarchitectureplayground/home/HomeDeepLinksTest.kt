package si.inova.androidarchitectureplayground.home

import android.net.Uri
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import si.inova.androidarchitectureplayground.navigation.conditions.ReplaceBackstackOrOpenScreenWithLogin
import si.inova.androidarchitectureplayground.navigation.keys.HomeScreenKey

class HomeDeepLinksTest {
   private val homeDeepLinks = HomeDeepLinks()

   @Test
   fun `handle posts link`() {
      homeDeepLinks.handleDeepLink(Uri.parse("demoapp://posts"), true) shouldBe
         ReplaceBackstackOrOpenScreenWithLogin(true, HomeScreenKey(HomeScreenKey.Tab.POSTS))

      homeDeepLinks.handleDeepLink(Uri.parse("demoapp://posts"), false) shouldBe
         ReplaceBackstackOrOpenScreenWithLogin(false, HomeScreenKey(HomeScreenKey.Tab.POSTS))
   }

   @Test
   fun `handle users link`() {
      homeDeepLinks.handleDeepLink(Uri.parse("demoapp://users"), true) shouldBe
         ReplaceBackstackOrOpenScreenWithLogin(true, HomeScreenKey(HomeScreenKey.Tab.USERS))

      homeDeepLinks.handleDeepLink(Uri.parse("demoapp://users"), false) shouldBe
         ReplaceBackstackOrOpenScreenWithLogin(false, HomeScreenKey(HomeScreenKey.Tab.USERS))
   }

   @Test
   fun `handle link to specific user`() {
      homeDeepLinks.handleDeepLink(Uri.parse("demoapp://users/2"), true) shouldBe
         ReplaceBackstackOrOpenScreenWithLogin(
            true,
            HomeScreenKey(HomeScreenKey.Tab.USERS, userDetailsId = "2")
         )

      homeDeepLinks.handleDeepLink(Uri.parse("demoapp://users/2"), false) shouldBe
         ReplaceBackstackOrOpenScreenWithLogin(
            false,
            HomeScreenKey(HomeScreenKey.Tab.USERS, userDetailsId = "2")
         )
   }

   @Test
   fun `handle settings`() {
      homeDeepLinks.handleDeepLink(Uri.parse("demoapp://settings"), true) shouldBe
         ReplaceBackstackOrOpenScreenWithLogin(true, HomeScreenKey(HomeScreenKey.Tab.SETTINGS))

      homeDeepLinks.handleDeepLink(Uri.parse("demoapp://settings"), false) shouldBe
         ReplaceBackstackOrOpenScreenWithLogin(false, HomeScreenKey(HomeScreenKey.Tab.SETTINGS))
   }

   @Test
   fun `Return null on invalid links`() {
      homeDeepLinks.handleDeepLink(Uri.parse("https://www.google.com"), true).shouldBeNull()
      homeDeepLinks.handleDeepLink(Uri.parse("demoapp://nolink"), true).shouldBeNull()
   }
}
