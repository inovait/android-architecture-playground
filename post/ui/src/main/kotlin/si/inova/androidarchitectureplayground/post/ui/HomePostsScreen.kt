package si.inova.androidarchitectureplayground.post.ui

import androidx.compose.runtime.Composable
import si.inova.androidarchitectureplayground.navigation.keys.HomePostsScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.PostDetailsScreenKey
import si.inova.androidarchitectureplayground.post.ui.list.PostListScreen
import si.inova.androidarchitectureplayground.ui.screens.MasterDetailScreen
import si.inova.kotlinova.navigation.di.ContributesScreenBinding
import si.inova.kotlinova.navigation.screens.Screen

@ContributesScreenBinding(boundType = Screen::class)
class HomePostsScreen(
   private val userListScreen: PostListScreen,
   private val userDetailsScreen: Screen<PostDetailsScreenKey>,
) : MasterDetailScreen<HomePostsScreenKey, PostDetailsScreenKey>() {
   @Composable
   override fun Master(key: HomePostsScreenKey, openDetail: (PostDetailsScreenKey) -> Unit) {
      userListScreen.Content { openDetail(PostDetailsScreenKey(it)) }
   }

   @Composable
   override fun Detail(key: PostDetailsScreenKey) {
      userDetailsScreen.Content(key)
   }

   override fun getDefaultOpenDetails(key: HomePostsScreenKey): PostDetailsScreenKey? {
      return key.userDetailsId?.toInt()?.let { PostDetailsScreenKey(it) }
   }
}
