package si.inova.androidarchitectureplayground.user.ui.details

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import si.inova.androidarchitectureplayground.navigation.keys.UserDetailsScreenKey
import si.inova.androidarchitectureplayground.ui.debugging.FullScreenPreviews
import si.inova.androidarchitectureplayground.ui.debugging.PreviewTheme
import si.inova.androidarchitectureplayground.ui.errors.commonUserFriendlyMessage
import si.inova.androidarchitectureplayground.user.model.User
import si.inova.kotlinova.compose.flow.collectAsStateWithLifecycleAndBlinkingPrevention
import si.inova.kotlinova.core.exceptions.NoNetworkException
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.di.ContributesScreenBinding
import si.inova.kotlinova.navigation.screens.InjectNavigationScreen
import si.inova.kotlinova.navigation.screens.Screen

@ContributesScreenBinding
@Stable
@InjectNavigationScreen
class UserDetailsScreen(
   private val viewModel: UserDetailsViewModel,
) : Screen<UserDetailsScreenKey>() {
   @Composable
   override fun Content(key: UserDetailsScreenKey) {
      remember(key.id) {
         viewModel.startLoading(key.id)
         true
      }
      val userOutcome = viewModel.userDetails.collectAsStateWithLifecycleAndBlinkingPrevention().value
      if (userOutcome != null) {
         UserDetailsContent(userOutcome, viewModel::refresh)
      }
   }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserDetailsContent(userOutcome: Outcome<User>, refresh: () -> Unit) {
   val refreshing = userOutcome is Outcome.Progress
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
         if (userOutcome is Outcome.Error) {
            Text(
               userOutcome.exception.commonUserFriendlyMessage(userOutcome.data != null),
               Modifier
                  .background(Color.Red)
                  .fillMaxWidth()
                  .padding(16.dp),
            )
         }

         val user = userOutcome.data
         if (user != null) {
            ShowUser(user)
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
private fun ShowUser(user: User) {
   val userText = with(user) {
      "$firstName $maidenName $lastName\n" +
         "$age, $gender\n" +
         "$email\n" +
         "$phone\n" +
         "${hair?.color} ${hair?.type} Hair"
   }

   Text(
      userText,
      Modifier
         .padding(8.dp)
         .fillMaxSize()
         .verticalScroll(rememberScrollState())
   )
}

@FullScreenPreviews
@ShowkaseComposable(group = "Test")
@Composable
internal fun UserDetailsScreenSuccesPreview() {
   val testUser = User(
      id = 1,
      firstName = "John",
      lastName = "Doe",
      maidenName = "Smith",
      age = 25,
      gender = "Apache Helicopter",
      email = "a@b.com",
      phone = "1234567890",
      hair = User.Hair(
         "brown",
         "curly"
      ),
   )

   PreviewTheme {
      UserDetailsContent(Outcome.Success(testUser), {})
   }
}

@FullScreenPreviews
@ShowkaseComposable(group = "Test")
@Composable
internal fun UserDetailsScreenProgressPreview() {
   val testUser = User(
      id = 1,
      firstName = "John",
      lastName = "Doe",
      maidenName = "Smith",
      age = 25,
      gender = "Apache Helicopter",
      email = "a@b.com",
      phone = "1234567890",
      hair = User.Hair(
         "brown",
         "curly"
      ),
   )

   PreviewTheme {
      UserDetailsContent(Outcome.Progress(testUser), {})
   }
}

@FullScreenPreviews
@ShowkaseComposable(group = "Test")
@Composable
internal fun UserDetailsScreenErrorPreview() {
   val testUser = User(
      id = 1,
      firstName = "John",
      lastName = "Doe",
      maidenName = "Smith",
      age = 25,
      gender = "Apache Helicopter",
      email = "a@b.com",
      phone = "1234567890",
      hair = User.Hair(
         "brown",
         "curly"
      ),
   )

   PreviewTheme {
      UserDetailsContent(Outcome.Error(NoNetworkException(), testUser), {})
   }
}
