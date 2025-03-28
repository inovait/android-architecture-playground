package si.inova.androidarchitectureplayground.post.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.airbnb.android.showkase.annotation.ShowkaseComposable
import si.inova.androidarchitectureplayground.navigation.keys.PostDetailsScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.UserDetailsScreenKey
import si.inova.androidarchitectureplayground.post.model.Post
import si.inova.androidarchitectureplayground.ui.debugging.FullScreenPreviews
import si.inova.androidarchitectureplayground.ui.debugging.PreviewTheme
import si.inova.androidarchitectureplayground.ui.errors.commonUserFriendlyMessage
import si.inova.architectureplayground.post.R
import si.inova.kotlinova.compose.flow.collectAsStateWithLifecycleAndBlinkingPrevention
import si.inova.kotlinova.core.exceptions.NoNetworkException
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.di.ContributesScreenBinding
import si.inova.kotlinova.navigation.instructions.navigateTo
import si.inova.kotlinova.navigation.navigator.Navigator
import si.inova.kotlinova.navigation.screens.InjectNavigationScreen
import si.inova.kotlinova.navigation.screens.Screen

@ContributesScreenBinding
@Stable
@InjectNavigationScreen
class PostDetailsScreen(
   private val viewModel: PostDetailsViewModel,
   private val navigator: Navigator,
) : Screen<PostDetailsScreenKey>() {
   @Composable
   override fun Content(key: PostDetailsScreenKey) {
      remember(key.id) {
         viewModel.startLoading(key.id)
         true
      }
      val postOutcome = viewModel.postDetails.collectAsStateWithLifecycleAndBlinkingPrevention().value
      if (postOutcome != null) {
         PostDetailsContent(postOutcome, viewModel::refresh) {
            navigator.navigateTo(UserDetailsScreenKey(it))
         }
      }
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostDetailsContent(
   postOutcome: Outcome<Post>,
   refresh: () -> Unit,
   navigateToUserDetails: (Int) -> Unit,
) {
   val refreshing = postOutcome is Outcome.Progress
   val topWindowOffset = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding()

   val refreshState = rememberPullToRefreshState()

   Box(
      Modifier.pullToRefresh(
         refreshing,
         refreshState,
         onRefresh = refresh,
         threshold = topWindowOffset + 48.dp
      )
   ) {
      Column(Modifier.safeDrawingPadding()) {
         if (postOutcome is Outcome.Error) {
            Text(
               postOutcome.exception.commonUserFriendlyMessage(postOutcome.data != null),
               Modifier
                  .background(Color.Red)
                  .fillMaxWidth()
                  .padding(16.dp),
            )
         }

         val post = postOutcome.data
         if (post != null) {
            ShowPost(post, navigateToUserDetails)
         }
      }

      PullToRefreshDefaults.Indicator(
         state = refreshState,
         modifier = Modifier.align(Alignment.TopCenter),
         isRefreshing = refreshing,
         threshold = topWindowOffset + 48.dp,
      )
   }
}

@Composable
@Suppress("NullableToStringCall") // This string is just a demo
private fun ShowPost(post: Post, navigateToUserDetails: (Int) -> Unit) {
   val postText = with(post) {
      "$title\n" +
         "$body\n" +
         "$numReactions reactions\n" +
         "$tags: $tags"
   }

   Column(
      Modifier
         .fillMaxSize()
         .verticalScroll(rememberScrollState())
   ) {
      post.image?.let {
         AsyncImage(
            it,
            contentDescription = null,
            modifier = Modifier
               .size(256.dp)
               .padding(32.dp)
         )
      }
      Text(
         postText,
         Modifier
            .padding(8.dp)
            .fillMaxSize()
      )

      post.userId?.let { userId ->
         Button(onClick = { navigateToUserDetails(userId) }) {
            Text(stringResource(R.string.open_author))
         }
      }
   }
}

@FullScreenPreviews
@ShowkaseComposable(group = "Test")
@Composable
internal fun PostDetailsScreenSuccesPreview() {
   val testPost = Post(
      id = 11,
      title = "She was aware that things could go wrong.",
      body = "She was aware that things could go wrong. In fact, she had trained her entire life in anticipation that...",
      userId = 26,
      tags = listOf("love", "english"),
      numReactions = 7,
      image = "https://i.dummyjson.com/data/products/12/1.jpg"
   )

   PreviewTheme {
      PostDetailsContent(Outcome.Success(testPost), {}, {})
   }
}

@FullScreenPreviews
@ShowkaseComposable(group = "Test")
@Composable
internal fun PostDetailsScreenProgressPreview() {
   val testPost = Post(
      id = 11,
      title = "She was aware that things could go wrong.",
      body = "She was aware that things could go wrong. In fact, she had trained her entire life in anticipation that...",
      userId = 26,
      tags = listOf("love", "english"),
      numReactions = 7,
      image = "https://i.dummyjson.com/data/products/12/1.jpg"
   )

   PreviewTheme {
      PostDetailsContent(Outcome.Progress(testPost), {}, {})
   }
}

@FullScreenPreviews
@ShowkaseComposable(group = "Test")
@Composable
internal fun PostDetailsScreenErrorPreview() {
   val testPost = Post(
      id = 11,
      title = "She was aware that things could go wrong.",
      body = "She was aware that things could go wrong. In fact, she had trained her entire life in anticipation that...",
      userId = 26,
      tags = listOf("love", "english"),
      numReactions = 7,
      image = "https://i.dummyjson.com/data/products/12/1.jpg"
   )

   PreviewTheme {
      PostDetailsContent(Outcome.Error(NoNetworkException(), testPost), {}, {})
   }
}
