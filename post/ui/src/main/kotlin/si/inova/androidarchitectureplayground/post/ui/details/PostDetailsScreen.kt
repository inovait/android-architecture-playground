package si.inova.androidarchitectureplayground.post.ui.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import si.inova.androidarchitectureplaygroud.post.model.Post
import si.inova.androidarchitectureplayground.navigation.keys.PostDetailsScreenKey
import si.inova.androidarchitectureplayground.ui.debugging.FullScreenPreview
import si.inova.androidarchitectureplayground.ui.debugging.PreviewTheme
import si.inova.androidarchitectureplayground.ui.errors.commonUserFriendlyMessage
import si.inova.kotlinova.compose.flow.collectAsStateWithLifecycleAndBlinkingPrevention
import si.inova.kotlinova.core.exceptions.NoNetworkException
import si.inova.kotlinova.core.outcome.Outcome
import si.inova.kotlinova.navigation.di.ContributesScreenBinding
import si.inova.kotlinova.navigation.screens.Screen

@ContributesScreenBinding
class PostDetailsScreen(
   private val viewModel: PostDetailsViewModel
) : Screen<PostDetailsScreenKey>() {
   @Composable
   override fun Content(key: PostDetailsScreenKey) {
      remember(key.id) {
         viewModel.startLoading(key.id)
         true
      }
      val postOutcome = viewModel.postDetails.collectAsStateWithLifecycleAndBlinkingPrevention().value
      if (postOutcome != null) {
         PostDetailsContent(postOutcome, viewModel::refresh)
      }
   }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun PostDetailsContent(postOutcome: Outcome<Post>, refresh: () -> Unit) {
   val refreshing = postOutcome is Outcome.Progress
   val refreshState = rememberPullRefreshState(
      refreshing = refreshing,
      onRefresh = { refresh() }
   )

   Box(Modifier.pullRefresh(refreshState)) {
      Column {
         if (postOutcome is Outcome.Error) {
            Text(
               postOutcome.exception.commonUserFriendlyMessage(),
               Modifier
                  .background(Color.Red)
                  .fillMaxWidth()
                  .padding(16.dp),
            )
         }

         val post = postOutcome.data
         if (post != null) {
            ShowPost(post)
         }
      }

      PullRefreshIndicator(
         refreshing = refreshing,
         state = refreshState,
         modifier = Modifier.align(Alignment.TopCenter)
      )
   }
}

@Composable
@Suppress("NullableToStringCall") // This string is just a demo
private fun ShowPost(post: Post) {
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
   }
}

@FullScreenPreview
@Composable
private fun SuccesPreview() {
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
      PostDetailsContent(Outcome.Success(testPost), {})
   }
}

@FullScreenPreview
@Composable
private fun ProgressPreview() {
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
      PostDetailsContent(Outcome.Progress(testPost), {})
   }
}

@FullScreenPreview
@Composable
private fun ErrorPreview() {
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
      PostDetailsContent(Outcome.Error(NoNetworkException(), testPost), {})
   }
}
