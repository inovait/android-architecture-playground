package si.inova.androidarchitectureplayground.util

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithText

/**
 * Wait for a node to show up and then execute [onNodeWithText].
 */
@OptIn(ExperimentalTestApi::class)
fun ComposeTestRule.onAwaitingNodeWithText(
   text: String,
   substring: Boolean = false,
   ignoreCase: Boolean = false,
   useUnmergedTree: Boolean = false,
   timeoutMs: Long = 5_000,
): SemanticsNodeInteraction {
   waitUntilAtLeastOneExists(hasText(text, substring, ignoreCase), timeoutMillis = timeoutMs)
   return onNodeWithText(text, substring, ignoreCase, useUnmergedTree)
}
