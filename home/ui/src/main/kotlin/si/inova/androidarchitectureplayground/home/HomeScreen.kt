package si.inova.androidarchitectureplayground.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation3.runtime.NavEntry
import com.zhuinden.simplestack.Backstack
import si.inova.androidarchitectureplayground.navigation.keys.HomePostsScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.HomeScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.HomeUsersScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.ManageProfileScreenKey
import si.inova.androidarchitectureplayground.navigation.scenes.LocalTabSelection
import si.inova.kotlinova.core.activity.requireActivity
import si.inova.kotlinova.navigation.instructions.replaceTopWith
import si.inova.kotlinova.navigation.navigation3.key
import si.inova.kotlinova.navigation.navigator.Navigator
import si.inova.kotlinova.navigation.screenkeys.ScreenKey
import si.inova.kotlinova.navigation.screens.InjectNavigationScreen
import si.inova.kotlinova.navigation.screens.Screen

@InjectNavigationScreen
class HomeScreen(
   private val navigator: Navigator,
   private val backstack: Backstack,
   // private val usersScreen: Screen<HomeUsersScreenKey>,
   // private val postsScreen: Screen<HomePostsScreenKey>,
   // private val manageProfileScreen: Screen<ManageProfileScreenKey>,
) : Screen<HomeScreenKey>() {
   @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
   @Composable
   override fun Content(key: HomeScreenKey) {
      val sizeClass = calculateWindowSizeClass(activity = LocalContext.current.requireActivity())

      this.Content(LocalTabSelection.current, sizeClass.widthSizeClass == WindowWidthSizeClass.Expanded)
   }

   @Composable
   private fun Content(selectedTab: NavEntry<ScreenKey>, useNavigationRail: Boolean) {
      val selectedKey = selectedTab.key()

      if (useNavigationRail) {
         NavigationRailContent(selectedTab::Content, selectedKey)
      } else {
         NavigationBarContent(selectedTab::Content, selectedKey)
      }
   }

   @Composable
   private fun NavigationBarContent(
      mainContent: @Composable () -> Unit,
      key: ScreenKey,
   ) {
      Column {
         Box(
            Modifier
               .fillMaxWidth()
               .weight(1f)
         ) {
            mainContent()
         }

         NavigationBar {
            NavigationBarItem(
               selected = key is HomePostsScreenKey,
               onClick = { navigator.replaceTopWith(HomePostsScreenKey()) },
               icon = { Icon(painter = painterResource(id = R.drawable.ic_posts), contentDescription = null) },
               label = { Text(stringResource(R.string.posts)) }
            )

            NavigationBarItem(
               selected = key is HomeUsersScreenKey,
               onClick = { navigator.replaceTopWith(HomeUsersScreenKey()) },
               icon = { Icon(painter = painterResource(id = R.drawable.ic_users), contentDescription = null) },
               label = { Text(stringResource(R.string.users)) }
            )

            NavigationBarItem(
               selected = key is ManageProfileScreenKey,
               onClick = { navigator.replaceTopWith(ManageProfileScreenKey) },
               icon = { Icon(painter = painterResource(id = R.drawable.ic_settings), contentDescription = null) },
               label = { Text(stringResource(R.string.settings)) }
            )
         }
      }
   }

   @Composable
   private fun NavigationRailContent(
      mainContent: @Composable () -> Unit,
      key: ScreenKey,
   ) {
      Row {
         NavigationRail {
            NavigationRailItem(
               selected = key is HomePostsScreenKey,
               onClick = { navigator.replaceTopWith(HomePostsScreenKey()) },
               icon = { Icon(painter = painterResource(id = R.drawable.ic_posts), contentDescription = null) },
               label = { Text(stringResource(R.string.posts)) }
            )

            NavigationRailItem(
               selected = key is HomeUsersScreenKey,
               onClick = { navigator.replaceTopWith(HomeUsersScreenKey()) },
               icon = { Icon(painter = painterResource(id = R.drawable.ic_users), contentDescription = null) },
               label = { Text(stringResource(R.string.users)) }
            )

            NavigationRailItem(
               selected = key is ManageProfileScreenKey,
               onClick = { navigator.replaceTopWith(ManageProfileScreenKey) },
               icon = { Icon(painter = painterResource(id = R.drawable.ic_settings), contentDescription = null) },
               label = { Text(stringResource(R.string.settings)) }
            )
         }

         Box(
            Modifier
               .fillMaxHeight()
               .weight(1f)
         ) {
            mainContent()
         }
      }
   }
}
