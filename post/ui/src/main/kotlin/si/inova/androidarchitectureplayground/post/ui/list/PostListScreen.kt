package si.inova.androidarchitectureplayground.post.ui.list

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.airbnb.android.showkase.annotation.ShowkaseComposable
import si.inova.androidarchitectureplaygroud.post.model.Post
import si.inova.androidarchitectureplayground.navigation.keys.PostDetailsScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.PostListScreenKey
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
import si.inova.kotlinova.navigation.screens.Screen

@ContributesScreenBinding
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PostListContent(
   state: Outcome<PostListState>,
   loadMore: () -> Unit,
   refresh: () -> Unit,
   openPostDetails: (id: Int) -> Unit,
) {
   val refreshing = state is Outcome.Progress && state.style != LoadingStyle.ADDITIONAL_DATA
   val refreshState = rememberPullRefreshState(
      refreshing = refreshing,
      onRefresh = { refresh() }
   )

   val lazyListState = rememberLazyListState()
   lazyListState.DetectScrolledToBottom(loadMore)

   Box(Modifier.pullRefresh(refreshState)) {
      Column {
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

      PullRefreshIndicator(
         refreshing = refreshing,
         state = refreshState,
         modifier = Modifier.align(Alignment.TopCenter)
      )
   }
}

@Composable
private fun ColumnScope.PostList(
   lazyListState: LazyListState,
   state: Outcome<PostListState>,
   openPostDetails: (id: Int) -> Unit,
) {
   LazyColumn(
      Modifier
         .fillMaxWidth()
         .weight(1f),
      lazyListState
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
internal fun PostListContentSuccess() {
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
internal fun PostListContentLoading() {
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
internal fun PostListContentError() {
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
internal fun PostListContentLoadingMore() {
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
