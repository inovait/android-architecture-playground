package si.inova.androidarchitectureplayground.post.ui.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import si.inova.androidarchitectureplaygroud.post.exceptions.UnknownPostException
import si.inova.androidarchitectureplaygroud.post.model.Post
import si.inova.androidarchitectureplayground.navigation.keys.PostScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.UserDetailsScreenKey
import si.inova.androidarchitectureplayground.post.ui.postUserFriendlyMessage
import si.inova.androidarchitectureplayground.ui.debugging.FullScreenPreview
import si.inova.androidarchitectureplayground.ui.debugging.PreviewTheme
import si.inova.kotlinova.compose.flow.collectAsStateWithLifecycleAndBlinkingPrevention
import si.inova.kotlinova.core.exceptions.NoNetworkException
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.instructions.navigateTo
import si.inova.kotlinova.navigation.navigator.Navigator
import si.inova.kotlinova.navigation.screens.Screen

class PostScreen(
   private val viewModel: PostScreenViewModel,
   private val navigator: Navigator
) : Screen<PostScreenKey>() {
   @Composable
   override fun Content(key: PostScreenKey) {
      Surface {
         val outcome = viewModel.post.collectAsStateWithLifecycleAndBlinkingPrevention().value
         if (outcome != null) {
            PostScreenContent(postOutcome = outcome) {
               navigator.navigateTo(UserDetailsScreenKey(it))
            }
         }
      }
   }
}

@Composable
private fun PostScreenContent(postOutcome: Outcome<Post>, openUserDetails: (id: Int) -> Unit) {
   Column {
      if (postOutcome is Outcome.Progress) {
         CircularProgressIndicator()
      } else if (postOutcome is Outcome.Error) {
         val errorMessage = postOutcome.exception.postUserFriendlyMessage()

         Text(
            errorMessage,
            Modifier
               .background(Color.Red)
               .padding(32.dp)
         )
      }

      val post = postOutcome.data
      if (post != null) {
         Text(post.title)
         Text(post.body ?: "")

         val userId = post.userId
         if (userId != null) {
            Button(onClick = { openUserDetails(post.id) }) {
               Text("Open post author")
            }
         }
      }
   }
}

@FullScreenPreview
@Composable
private fun PostScreenSuccessPreview() {
   val post = Outcome.Success(
      Post(
         id = 2,
         title = "hello",
         body = "Text"
      )
   )

   PreviewTheme {
      PostScreenContent(postOutcome = post, {})
   }
}

@FullScreenPreview
@Composable
private fun PostScreenProgressPreview() {
   val post = Outcome.Progress<Post>()

   PreviewTheme {
      PostScreenContent(postOutcome = post, {})
   }
}

@FullScreenPreview
@Composable
private fun PostScreenErrorPreview() {
   val post = Outcome.Error<Post>(UnknownPostException())

   PreviewTheme {
      PostScreenContent(postOutcome = post, {})
   }
}

@FullScreenPreview
@Composable
private fun PostScreenErrorPreviewWithData() {
   val post = Outcome.Error<Post>(
      NoNetworkException(),
      Post(
         id = 2,
         title = "hello",
         body = "Text"
      )
   )

   PreviewTheme {
      PostScreenContent(postOutcome = post, {})
   }
}
