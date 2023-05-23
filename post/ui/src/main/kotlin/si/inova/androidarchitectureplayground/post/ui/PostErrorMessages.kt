package si.inova.androidarchitectureplayground.post.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import si.inova.androidarchitectureplaygroud.post.exceptions.UnknownPostException
import si.inova.androidarchitectureplayground.ui.errors.commonUserFriendlyMessage
import si.inova.kotlinova.core.outcome.CauseException

@Composable
fun CauseException.postUserFriendlyMessage(): String {
   return when (this) {
      is UnknownPostException -> stringResource(si.inova.architectureplayground.post.R.string.error_unknown_post)
      else -> commonUserFriendlyMessage()
   }
}
