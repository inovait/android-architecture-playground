package si.inova.androidarchitectureplayground.ui.errors

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import si.inova.androidarchitectureplayground.common.exceptions.NoNetworkException
import si.inova.androidarchitectureplayground.common.outcome.CauseException
import si.inova.androidarchitectureplayground.ui.R

@Composable
fun CauseException.commonUserFriendlyMessage(): String {
   return when (this) {
      is NoNetworkException -> stringResource(R.string.error_no_network)
      else -> stringResource(R.string.error_unknown)
   }
}
