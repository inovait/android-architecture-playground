package si.inova.androidarchitectureplayground.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import si.inova.androidarchitectureplayground.navigation.keys.HomeScreenKey
import si.inova.kotlinova.navigation.instructions.navigateTo
import si.inova.kotlinova.navigation.navigator.Navigator
import si.inova.kotlinova.navigation.screens.Screen

class HomeScreen(
   private val navigator: Navigator
) : Screen<HomeScreenKey>() {
   @Composable
   override fun Content(key: HomeScreenKey) {
      Column {
         Box(
            Modifier
               .fillMaxWidth()
               .weight(1f)
         ) {
            key(key.selectedTab) {
               Text(key.selectedTab.toString())
            }
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
         }
      }
   }
}
