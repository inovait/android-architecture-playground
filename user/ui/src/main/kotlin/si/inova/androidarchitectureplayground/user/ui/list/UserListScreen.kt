package si.inova.androidarchitectureplayground.user.ui.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshDefaults
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.airbnb.android.showkase.annotation.ShowkaseComposable
import si.inova.androidarchitectureplayground.navigation.keys.UserDetailsScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.UserListScreenKey
import si.inova.androidarchitectureplayground.ui.debugging.FullScreenPreviews
import si.inova.androidarchitectureplayground.ui.debugging.PreviewTheme
import si.inova.androidarchitectureplayground.ui.errors.commonUserFriendlyMessage
import si.inova.androidarchitectureplayground.ui.lists.DetectScrolledToBottom
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.architectureplayground.user.R
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
@Stable
class UserListScreen(
   private val viewModel: UserListViewModel,
   private val navigator: Navigator,
) : Screen<UserListScreenKey>() {
   @Composable
   override fun Content(key: UserListScreenKey) {
      Content {
         navigator.navigateTo(UserDetailsScreenKey(it))
      }
   }

   @Composable
   fun Content(navigate: (id: Int) -> Unit) {
      val data = viewModel.userList.collectAsStateWithLifecycleAndBlinkingPrevention(
         doNotWaitForInterimLoadings = true
      ).value

      if (data != null) {
         UserListContent(
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
private fun UserListContent(
   state: Outcome<UserListState>,
   loadMore: () -> Unit,
   refresh: () -> Unit,
   openUserDetails: (id: Int) -> Unit,
) {
   val refreshing = state is Outcome.Progress && state.style != LoadingStyle.ADDITIONAL_DATA
   val topWindowOffset = WindowInsets.safeDrawing.asPaddingValues().calculateTopPadding()

   val refreshState = rememberPullRefreshState(
      refreshing = refreshing,
      onRefresh = { refresh() },
      refreshThreshold = PullRefreshDefaults.RefreshThreshold + topWindowOffset,
      refreshingOffset = PullRefreshDefaults.RefreshingOffset + topWindowOffset
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

         UserList(lazyListState, state, openUserDetails)
      }

      PullRefreshIndicator(
         refreshing = refreshing,
         state = refreshState,
         modifier = Modifier.align(Alignment.TopCenter)
      )
   }
}

@Composable
private fun ColumnScope.UserList(
   lazyListState: LazyListState,
   state: Outcome<UserListState>,
   openUserDetails: (id: Int) -> Unit,
) {
   LazyColumn(
      Modifier
         .fillMaxWidth()
         .weight(1f),
      lazyListState,
      contentPadding = WindowInsets.safeDrawing.asPaddingValues()
   ) {
      itemsWithDivider(state.data?.users.orEmpty()) {
         Text(
            stringResource(R.string.first_last_name, it.firstName, it.lastName),
            Modifier
               .clickable { openUserDetails(it.id) }
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
internal fun UserListContentSuccess() {
   PreviewTheme {
      UserListContent(
         state = Outcome.Success(
            UserListState(
               List<User>(20) {
                  User(it, "First $it", "Last $it")
               }
            )
         ),
         loadMore = { },
         refresh = { },
         openUserDetails = {}
      )
   }
}

@FullScreenPreviews
@ShowkaseComposable(group = "Test")
@Composable
internal fun UserListContentLoading() {
   PreviewTheme {
      UserListContent(
         state = Outcome.Progress(
            UserListState(
               List<User>(20) {
                  User(it, "First $it", "Last $it")
               }
            )
         ),
         loadMore = { },
         refresh = { },
         openUserDetails = {}
      )
   }
}

@FullScreenPreviews
@ShowkaseComposable(group = "Test")
@Composable
internal fun UserListContentError() {
   PreviewTheme {
      UserListContent(
         state = Outcome.Error(
            NoNetworkException(),
            UserListState(
               List<User>(20) {
                  User(it, "First $it", "Last $it")
               }
            )
         ),
         loadMore = { },
         refresh = { },
         openUserDetails = {}
      )
   }
}

@FullScreenPreviews
@ShowkaseComposable(group = "Test")
@Composable
internal fun UserListContentLoadingMore() {
   PreviewTheme {
      UserListContent(
         state = Outcome.Progress(
            UserListState(
               List<User>(3) {
                  User(it, "First $it", "Last $it")
               },
               hasAnyDataLeft = true
            ),
            style = LoadingStyle.ADDITIONAL_DATA
         ),
         loadMore = { },
         refresh = { },
         openUserDetails = {}
      )
   }
}
