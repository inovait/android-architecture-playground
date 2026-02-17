package si.inova.androidarchitectureplayground.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.airbnb.android.showkase.annotation.ShowkaseComposable
import si.inova.androidarchitectureplayground.navigation.instructions.ReplaceTabContentWith
import si.inova.androidarchitectureplayground.navigation.keys.HomeScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.ManageProfileScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.PostListScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.UserListScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.base.LocalSelectedTabContent
import si.inova.androidarchitectureplayground.navigation.keys.base.SelectedTabContent
import si.inova.androidarchitectureplayground.ui.debugging.FullScreenPreviews
import si.inova.kotlinova.core.activity.requireActivity
import si.inova.kotlinova.navigation.instructions.NavigationInstruction
import si.inova.kotlinova.navigation.navigator.Navigator
import si.inova.kotlinova.navigation.screenkeys.ScreenKey
import si.inova.kotlinova.navigation.screens.InjectNavigationScreen
import si.inova.kotlinova.navigation.screens.Screen

@InjectNavigationScreen
class HomeScreen(
   private val navigator: Navigator,
) : Screen<HomeScreenKey>() {
   @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
   @Composable
   override fun Content(key: HomeScreenKey) {
      val sizeClass = calculateWindowSizeClass(activity = LocalContext.current.requireActivity())

      Content(
         LocalSelectedTabContent.current,
         sizeClass.widthSizeClass == WindowWidthSizeClass.Expanded,
         navigator::navigate
      )
   }
}

@Composable
private fun Content(selectedTab: SelectedTabContent, useNavigationRail: Boolean, navigate: (NavigationInstruction) -> Unit) {
   val animatedMainContent: @Composable () -> Unit = {
      AnimatedContent(
         selectedTab,
         contentKey = { entry -> entry.key },
         transitionSpec = { fadeIn() togetherWith fadeOut() }
      ) {
         it.content()
      }
   }

   if (useNavigationRail) {
      NavigationRailContent(selectedTab.key, navigate, animatedMainContent)
   } else {
      NavigationBarContent(selectedTab.key, navigate, animatedMainContent)
   }
}

@Composable
private fun NavigationBarContent(
   key: ScreenKey,
   navigate: (NavigationInstruction) -> Unit,
   mainContent: @Composable () -> Unit,
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
            selected = key is PostListScreenKey,
            onClick = { navigate(ReplaceTabContentWith(PostListScreenKey)) },
            icon = { Icon(painter = painterResource(id = R.drawable.ic_posts), contentDescription = null) },
            label = { Text(stringResource(R.string.posts)) }
         )

         NavigationBarItem(
            selected = key is UserListScreenKey,
            onClick = { navigate(ReplaceTabContentWith(UserListScreenKey)) },
            icon = { Icon(painter = painterResource(id = R.drawable.ic_users), contentDescription = null) },
            label = { Text(stringResource(R.string.users)) }
         )

         NavigationBarItem(
            selected = key is ManageProfileScreenKey,
            onClick = { navigate(ReplaceTabContentWith(ManageProfileScreenKey)) },
            icon = { Icon(painter = painterResource(id = R.drawable.ic_settings), contentDescription = null) },
            label = { Text(stringResource(R.string.settings)) }
         )
      }
   }
}

@Composable
private fun NavigationRailContent(
   key: ScreenKey,
   navigate: (NavigationInstruction) -> Unit,
   mainContent: @Composable () -> Unit,
) {
   Row {
      NavigationRail {
         NavigationRailItem(
            selected = key is PostListScreenKey,
            onClick = { navigate(ReplaceTabContentWith(PostListScreenKey)) },
            icon = { Icon(painter = painterResource(id = R.drawable.ic_posts), contentDescription = null) },
            label = { Text(stringResource(R.string.posts)) }
         )

         NavigationRailItem(
            selected = key is UserListScreenKey,
            onClick = { navigate(ReplaceTabContentWith(UserListScreenKey)) },
            icon = { Icon(painter = painterResource(id = R.drawable.ic_users), contentDescription = null) },
            label = { Text(stringResource(R.string.users)) }
         )

         NavigationRailItem(
            selected = key is ManageProfileScreenKey,
            onClick = { navigate(ReplaceTabContentWith(ManageProfileScreenKey)) },
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

@FullScreenPreviews
@Composable
@ShowkaseComposable(group = "test")
internal fun HomeScreenPhonePreview() {
   Content(
      SelectedTabContent(
         {
            Box(
               Modifier
                  .fillMaxSize()
                  .background(Color.Magenta)
            )
         },
         PostListScreenKey
      ),
      false,
      {}
   )
}

@Preview(device = Devices.TABLET)
@Composable
@ShowkaseComposable(group = "test")
internal fun HomeScreenTabletPreview() {
   Content(
      SelectedTabContent(
         {
            Box(
               Modifier
                  .fillMaxSize()
                  .background(Color.Magenta)
            )
         },
         UserListScreenKey
      ),
      true,
      {}
   )
}
