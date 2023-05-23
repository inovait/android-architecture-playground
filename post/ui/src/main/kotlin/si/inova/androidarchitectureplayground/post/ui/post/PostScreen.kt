package si.inova.androidarchitectureplayground.post.ui.post

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import si.inova.androidarchitectureplayground.navigation.keys.PostScreenKey
import si.inova.kotlinova.navigation.screens.Screen

class PostScreen : Screen<PostScreenKey>() {
   @Composable
   override fun Content(key: PostScreenKey) {
      Surface {
      }
   }
}
