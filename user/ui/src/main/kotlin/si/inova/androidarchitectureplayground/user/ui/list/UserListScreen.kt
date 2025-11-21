package si.inova.androidarchitectureplayground.user.ui.list

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
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.airbnb.android.showkase.annotation.ShowkaseComposable
import si.inova.androidarchitectureplayground.navigation.keys.UserDetailsScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.UserListScreenKey
import si.inova.androidarchitectureplayground.paging.PagedList
import si.inova.androidarchitectureplayground.paging.pagedListOf
import si.inova.androidarchitectureplayground.ui.debugging.FullScreenPreviews
import si.inova.androidarchitectureplayground.ui.debugging.PreviewTheme
import si.inova.androidarchitectureplayground.ui.errors.commonUserFriendlyMessage
import si.inova.androidarchitectureplayground.ui.lists.DetectScrolledToBottom
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.architectureplayground.user.R
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
            {},
            viewModel::refresh,
            navigate
         )
      }
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserListContent(
   state: Outcome<PagedList<User>>,
   loadMore: () -> Unit,
   refresh: () -> Unit,
   openUserDetails: (id: Int) -> Unit,
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

         UserList(lazyListState, state, openUserDetails)
      }

      PullToRefreshDefaults.Indicator(
         state = refreshState,
         modifier = Modifier.align(Alignment.TopCenter),
         isRefreshing = refreshing,
         maxDistance = topWindowOffset + 48.dp,
      )
   }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ColumnScope.UserList(
   lazyListState: LazyListState,
   state: Outcome<PagedList<User>>,
   openUserDetails: (id: Int) -> Unit,
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
      itemsWithDivider(
         state.data ?: pagedListOf(),
         placeholderContent = {
            Box(
               Modifier
                  .fillMaxWidth()
                  .height(32.dp)
                  .animateItem(),
               Alignment.Center
            ) {}
         },
         key = { index, _ -> index }
      ) {
         Text(
            stringResource(R.string.first_last_name, it.firstName, it.lastName),
            Modifier
               .clickable { openUserDetails(it.id) }
               .fillMaxWidth()
               .padding(32.dp)
               .animateItem(),
         )
      }

      if (state is Outcome.Progress && state.style == LoadingStyle.ADDITIONAL_DATA) {
         item(key = (state.data?.size ?: 0) + 1) {
            Box(
               Modifier
                  .fillMaxWidth()
                  .height(32.dp)
                  .animateItem(),
               Alignment.Center,
            ) {
               CircularProgressIndicator(Modifier.size(32.dp))
            }
         }
      }
   }
}

@FullScreenPreviews
@ShowkaseComposable(group = "Test")
@Composable
internal fun UserListContentSuccessPreview() {
   PreviewTheme {
      UserListContent(
         state = Outcome.Success(
            pagedListOf(
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
internal fun UserListContentLoadingPreview() {
   PreviewTheme {
      UserListContent(
         state = Outcome.Progress(
            pagedListOf(
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
internal fun UserListContentErrorPreview() {
   PreviewTheme {
      UserListContent(
         state = Outcome.Error(
            NoNetworkException(),
            pagedListOf(
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
internal fun UserListContentLoadingMorePreview() {
   PreviewTheme {
      UserListContent(
         state = Outcome.Progress(
            pagedListOf(
               List<User>(3) {
                  User(it, "First $it", "Last $it")
               },
            ),
            style = LoadingStyle.ADDITIONAL_DATA
         ),
         loadMore = { },
         refresh = { },
         openUserDetails = {}
      )
   }
}

/**
 * Adds a list of items with dividers between them.
 *
 * @param items the data list
 * @param modifier Modifier for the entire combined item. Use for animations.
 * @param dividerContent the content displayed between each item
 * @param key a factory of stable and unique keys representing the item. Using the same key
 * for multiple items in the list is not allowed. Type of the key should be saveable
 * via Bundle on Android. If null is passed the position in the list will represent the key.
 * When you specify the key the scroll position will be maintained based on the key, which
 * means if you add/remove items before the current visible item the item with the given key
 * will be kept as the first visible one.
 * @param contentType a factory of the content types for the item. The item compositions of
 * the same type could be reused more efficiently. Note that null is a valid type and items of such
 * type will be considered compatible.
 * @param placeholderContent Placeholder for items that are not yet loaded
 * @param itemContent the content displayed by a single item
 */

private inline fun <T> LazyListScope.itemsWithDivider(
   items: PagedList<T>,
   crossinline modifier: LazyItemScope.(T?) -> Modifier = { Modifier },
   crossinline dividerContent: @Composable () -> Unit = { HorizontalDivider() },
   noinline key: ((index: Int, item: T?) -> Any)? = null,
   noinline contentType: (item: T?) -> Any? = { null },
   crossinline placeholderContent: @Composable LazyItemScope.() -> Unit,
   crossinline itemContent: @Composable LazyItemScope.(item: T) -> Unit,
) = items(
   count = items.size,
   key = if (key != null) { index: Int -> key(index, items.peek(index)) } else null,
   contentType = { index: Int -> contentType(items.peek(index)) }
) {
   val item = items[it]
   Column(modifier(item)) {
      if (item != null) {
         itemContent(item)
      } else {
         placeholderContent()
      }
      if (it < items.size - 1) {
         dividerContent()
      }
   }
}
