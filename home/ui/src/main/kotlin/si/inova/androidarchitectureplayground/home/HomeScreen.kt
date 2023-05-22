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
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import si.inova.androidarchitectureplayground.navigation.keys.HomeScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.HomeUsersScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.ManageProfileScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.UserDetailsScreenKey
import si.inova.androidarchitectureplayground.ui.screens.MasterDetailScreen
import si.inova.kotlinova.core.activity.requireActivity
import si.inova.kotlinova.navigation.instructions.navigateTo
import si.inova.kotlinova.navigation.navigator.Navigator
import si.inova.kotlinova.navigation.screens.Screen

class HomeScreen(
   private val navigator: Navigator,
   private val usersScreen: MasterDetailScreen<HomeUsersScreenKey, UserDetailsScreenKey>,
   private val manageProfileScreen: Screen<ManageProfileScreenKey>
) : Screen<HomeScreenKey>() {
   @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
   @Composable
   override fun Content(key: HomeScreenKey) {
      val sizeClass = calculateWindowSizeClass(activity = LocalContext.current.requireActivity())

      this.Content(key, sizeClass.widthSizeClass == WindowWidthSizeClass.Expanded)
   }

   @Composable
   private fun Content(key: HomeScreenKey, useNavigationRail: Boolean) {
      val keyState = rememberUpdatedState(key)

      val mainContent = remember {
         movableContentOf {
            MainContent(keyState.value)
         }
      }

      if (useNavigationRail) {
         NavigationRailContent(mainContent, key)
      } else {
         NavigationBarContent(mainContent, key)
      }
   }

   @Composable
   private fun MainContent(key: HomeScreenKey) {
      val tab = key.selectedTab
      val stateHolder = rememberSaveableStateHolder()
      stateHolder.SaveableStateProvider(tab) {
         when (tab) {
            HomeScreenKey.Tab.POSTS -> Text("Posts")
            HomeScreenKey.Tab.USERS -> usersScreen.Content(HomeUsersScreenKey(key.userDetailsId))
            HomeScreenKey.Tab.SETTINGS -> manageProfileScreen.Content(ManageProfileScreenKey)
         }
      }
   }

   @Composable
   private fun NavigationBarContent(
      mainContent: @Composable () -> Unit,
      key: HomeScreenKey
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
               selected = key.selectedTab == HomeScreenKey.Tab.POSTS,
               onClick = { navigator.navigateTo(key.copy(selectedTab = HomeScreenKey.Tab.POSTS)) },
               icon = { Icon(painter = painterResource(id = R.drawable.ic_posts), contentDescription = null) },
               label = { Text(stringResource(R.string.posts)) }
            )

            NavigationBarItem(
               selected = key.selectedTab == HomeScreenKey.Tab.USERS,
               onClick = { navigator.navigateTo(key.copy(selectedTab = HomeScreenKey.Tab.USERS)) },
               icon = { Icon(painter = painterResource(id = R.drawable.ic_users), contentDescription = null) },
               label = { Text(stringResource(R.string.users)) }
            )

            NavigationBarItem(
               selected = key.selectedTab == HomeScreenKey.Tab.SETTINGS,
               onClick = { navigator.navigateTo(key.copy(selectedTab = HomeScreenKey.Tab.SETTINGS)) },
               icon = { Icon(painter = painterResource(id = R.drawable.ic_settings), contentDescription = null) },
               label = { Text(stringResource(R.string.settings)) }
            )
         }
      }
   }

   @Composable
   private fun NavigationRailContent(
      mainContent: @Composable () -> Unit,
      key: HomeScreenKey
   ) {
      Row {
         NavigationRail {
            NavigationRailItem(
               selected = key.selectedTab == HomeScreenKey.Tab.POSTS,
               onClick = { navigator.navigateTo(key.copy(selectedTab = HomeScreenKey.Tab.POSTS)) },
               icon = { Icon(painter = painterResource(id = R.drawable.ic_posts), contentDescription = null) },
               label = { Text(stringResource(R.string.posts)) }
            )

            NavigationRailItem(
               selected = key.selectedTab == HomeScreenKey.Tab.USERS,
               onClick = { navigator.navigateTo(key.copy(selectedTab = HomeScreenKey.Tab.USERS)) },
               icon = { Icon(painter = painterResource(id = R.drawable.ic_users), contentDescription = null) },
               label = { Text(stringResource(R.string.users)) }
            )

            NavigationRailItem(
               selected = key.selectedTab == HomeScreenKey.Tab.SETTINGS,
               onClick = { navigator.navigateTo(key.copy(selectedTab = HomeScreenKey.Tab.SETTINGS)) },
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
