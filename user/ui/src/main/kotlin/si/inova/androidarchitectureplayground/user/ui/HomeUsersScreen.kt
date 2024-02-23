package si.inova.androidarchitectureplayground.user.ui

import androidx.compose.runtime.Composable
import si.inova.androidarchitectureplayground.navigation.keys.HomeUsersScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.UserDetailsScreenKey
import si.inova.androidarchitectureplayground.ui.screens.MasterDetailScreen
import si.inova.androidarchitectureplayground.user.ui.list.UserListScreen
import si.inova.kotlinova.navigation.di.ContributesScreenBinding
import si.inova.kotlinova.navigation.screens.Screen

@ContributesScreenBinding(boundType = Screen::class)
class HomeUsersScreen(
   private val userListScreen: UserListScreen,
   private val userDetailsScreen: Screen<UserDetailsScreenKey>,
) : MasterDetailScreen<HomeUsersScreenKey, UserDetailsScreenKey>() {
   @Composable
   override fun Master(key: HomeUsersScreenKey, openDetail: (UserDetailsScreenKey) -> Unit) {
      userListScreen.Content { openDetail(UserDetailsScreenKey(it)) }
   }

   @Composable
   override fun Detail(key: UserDetailsScreenKey) {
      userDetailsScreen.Content(key)
   }

   override fun getDefaultOpenDetails(key: HomeUsersScreenKey): UserDetailsScreenKey? {
      return key.userDetailsId?.toInt()?.let { UserDetailsScreenKey(it) }
   }
}
