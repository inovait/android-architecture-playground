package si.inova.androidarchitectureplayground.post.ui.list

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.MutableWindowInsets
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.onConsumedWindowInsetsChanged
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import com.airbnb.android.showkase.annotation.ShowkaseComposable
import si.inova.androidarchitectureplayground.navigation.keys.PostDetailsScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.PostListScreenKey
import si.inova.androidarchitectureplayground.post.model.Post
import si.inova.androidarchitectureplayground.ui.debugging.FullScreenPreviews
import si.inova.androidarchitectureplayground.ui.debugging.PreviewTheme
import si.inova.androidarchitectureplayground.ui.errors.commonUserFriendlyMessage
import si.inova.androidarchitectureplayground.ui.lists.DetectScrolledToBottom
import si.inova.kotlinova.compose.components.itemsWithDivider
import si.inova.kotlinova.compose.flow.collectAsStateWithLifecycleAndBlinkingPrevention
import si.inova.kotlinova.core.exceptions.NoNetworkException
import si.inova.kotlinova.core.outcome.LoadingStyle
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.di.ContributesScreenBinding
import si.inova.kotlinova.navigation.instructions.navigateTo
import si.inova.kotlinova.navigation.navigator.Navigator
import si.inova.kotlinova.navigation.screens.InjectNavigationScreen
import si.inova.kotlinova.navigation.screens.Screen

@ContributesScreenBinding
@Stable
@InjectNavigationScreen
class PostListScreen(
   private val viewModel: PostListViewModel,
   private val navigator: Navigator,
) : Screen<PostListScreenKey>() {
   @Composable
   override fun Content(key: PostListScreenKey) {
      Content {
         navigator.navigateTo(PostDetailsScreenKey(it))
      }
   }

   @Composable
   fun Content(navigate: (id: Int) -> Unit) {
      val data = viewModel.postList.collectAsStateWithLifecycleAndBlinkingPrevention(
         doNotWaitForInterimLoadings = true
      ).value

      ReportDrawnWhen { data != null && data !is Outcome.Progress }

      if (data != null) {
         PostListContent(
            data,
            viewModel::nextPage,
            viewModel::refresh,
            navigate
         )
      }
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PostListContent(
   state: Outcome<PostListState>,
   loadMore: () -> Unit,
   refresh: () -> Unit,
   openPostDetails: (id: Int) -> Unit,
) {
   val refreshing = state is Outcome.Progress && state.style != LoadingStyle.ADDITIONAL_DATA
   val topWindowOffset = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding()

   val refreshState = rememberPullToRefreshState()
   val lazyListState = rememberLazyListState()
   lazyListState.DetectScrolledToBottom(loadMore)

   Box(
      Modifier.pullToRefresh(
         refreshing,
         refreshState,
         onRefresh = refresh,
         threshold = topWindowOffset + 48.dp
      )
   ) {
      Column(
         if (state is Outcome.Error) {
            Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top))
         } else {
            Modifier
         }
      ) {
         if (state is Outcome.Error) {
            Text(
               state.exception.commonUserFriendlyMessage(state.data != null),
               Modifier
                  .background(Color.Red)
                  .fillMaxWidth()
                  .padding(16.dp),
            )
         }

         PostList(lazyListState, state, openPostDetails)
      }

      PullToRefreshDefaults.Indicator(
         state = refreshState,
         modifier = Modifier.align(Alignment.TopCenter),
         isRefreshing = refreshing,
         threshold = topWindowOffset + 48.dp,
      )
   }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColumnScope.PostList(
   lazyListState: LazyListState,
   state: Outcome<PostListState>,
   openPostDetails: (id: Int) -> Unit,
) {
   val consumedWindowInsets = remember { MutableWindowInsets() }

   LazyColumn(
      Modifier
         .fillMaxWidth()
         .weight(1f)
         .onConsumedWindowInsetsChanged {
            consumedWindowInsets.insets = it
         },
      lazyListState,
      contentPadding = WindowInsets.safeDrawing.exclude(consumedWindowInsets).asPaddingValues()
   ) {
      itemsWithDivider(state.data?.posts.orEmpty()) {
         Text(
            it.title,
            Modifier
               .clickable { openPostDetails(it.id) }
               .fillMaxWidth()
               .padding(32.dp),
         )
      }

      if (state.data?.hasAnyDataLeft == true) {
         item {
            Box(
               Modifier
                  .fillMaxWidth()
                  .height(32.dp),
               Alignment.Center
            ) {
               if (state is Outcome.Progress && state.style == LoadingStyle.ADDITIONAL_DATA) {
                  CircularProgressIndicator(Modifier.size(32.dp))
               }
            }
         }
      }
   }
}

@FullScreenPreviews
@ShowkaseComposable(group = "Test")
@Composable
internal fun PostListContentSuccessPreview() {
   PreviewTheme {
      PostListContent(
         state = Outcome.Success(
            PostListState(
               List<Post>(20) {
                  Post(it, "Post $it")
               }
            )
         ),
         loadMore = { },
         refresh = { },
         openPostDetails = {}
      )
   }
}

@FullScreenPreviews
@ShowkaseComposable(group = "Test")
@Composable
internal fun PostListContentLoadingPreview() {
   PreviewTheme {
      PostListContent(
         state = Outcome.Progress(
            PostListState(
               List<Post>(20) {
                  Post(it, "Post $it")
               }
            )
         ),
         loadMore = { },
         refresh = { },
         openPostDetails = {}
      )
   }
}

@FullScreenPreviews
@ShowkaseComposable(group = "Test")
@Composable
internal fun PostListContentErrorPreview() {
   PreviewTheme {
      PostListContent(
         state = Outcome.Error(
            NoNetworkException(),
            PostListState(
               List<Post>(20) {
                  Post(it, "Post $it")
               }
            )
         ),
         loadMore = { },
         refresh = { },
         openPostDetails = {}
      )
   }
}

@FullScreenPreviews
@ShowkaseComposable(group = "Test")
@Composable
internal fun PostListContentLoadingMorePreview() {
   PreviewTheme {
      PostListContent(
         state = Outcome.Progress(
            PostListState(
               List<Post>(3) {
                  Post(it, "Post $it")
               },
               hasAnyDataLeft = true
            ),
            style = LoadingStyle.ADDITIONAL_DATA
         ),
         loadMore = { },
         refresh = { },
         openPostDetails = {}
      )
   }
}
