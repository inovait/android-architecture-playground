package si.inova.androidarchitectureplayground.masterdetail

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import si.inova.androidarchitectureplayground.screens.MasterDetailDemoScreenKey
import si.inova.kotlinova.core.activity.requireActivity
import si.inova.kotlinova.navigation.screens.Screen

class MasterDetailDemoScreen : Screen<MasterDetailDemoScreenKey>() {
   @Composable
   @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
   override fun Content(key: MasterDetailDemoScreenKey) {
      val windowSize = calculateWindowSizeClass(LocalContext.current.requireActivity())
      MasterDetail(windowSize.widthSizeClass)
   }
}

@Composable
private fun MasterDetail(widthSize: WindowWidthSizeClass) {
   val currentDetailScreen = rememberSaveable { mutableStateOf("A") }
   val openState = rememberSaveable { mutableStateOf(false) }

   val master = movableContentOf<Modifier> { modifier ->
      Master(modifier) {
         currentDetailScreen.value = it
         openState.value = true
      }
   }

   val detail = movableContentOf<Modifier, String> { modifier, text ->
      Detail(modifier, text)
   }

   if (widthSize == WindowWidthSizeClass.Compact) {
      MasterDetailOnPhone(openState, currentDetailScreen, master, detail)
   } else {
      MasterDetailOnLargerScreen(currentDetailScreen, master, detail)
   }
}

@Composable
@OptIn(ExperimentalAnimationApi::class)
private fun MasterDetailOnPhone(
   openState: MutableState<Boolean>,
   currentDetailScreen: MutableState<String>,
   master: @Composable (Modifier) -> Unit,
   detail: @Composable (Modifier, String) -> Unit
) {
   AnimatedContent(openState.value, transitionSpec = {
      if (this.targetState) {
         slideIntoContainer(AnimatedContentScope.SlideDirection.Left) with
            slideOutOfContainer(AnimatedContentScope.SlideDirection.Left)
      } else {
         slideIntoContainer(AnimatedContentScope.SlideDirection.Right) with
            slideOutOfContainer(AnimatedContentScope.SlideDirection.Right)
      }
   }) { open ->
      if (open) {
         detail(Modifier.fillMaxSize(), currentDetailScreen.value)
      } else {
         master(Modifier.fillMaxSize())
      }
   }

   BackHandler(enabled = openState.value) {
      openState.value = false
   }
}

@Composable
private fun MasterDetailOnLargerScreen(
   currentDetailScreen: MutableState<String>,
   master: @Composable (Modifier) -> Unit,
   detail: @Composable (Modifier, String) -> Unit
) {
   Row(Modifier.fillMaxSize()) {
      master(
         Modifier
            .weight(1f)
            .fillMaxHeight()
      )

      Crossfade(
         currentDetailScreen.value,
         Modifier
            .weight(2f)
            .fillMaxHeight()
      ) { value ->
         detail(Modifier.fillMaxSize(), value)
      }
   }
}

@Composable
private fun Master(modifier: Modifier, switchDetail: (String) -> Unit) {
   Column(
      modifier
         .background(Color.Red)
         .padding(32.dp),
      verticalArrangement = Arrangement.spacedBy(32.dp),
      horizontalAlignment = Alignment.CenterHorizontally
   ) {
      Button(onClick = { switchDetail("A") }) {
         Text("Open Detail A")
      }

      Button(onClick = { switchDetail("B") }) {
         Text("Open Detail B")
      }

      Button(onClick = { switchDetail("C") }) {
         Text("Open Detail C")
      }
   }
}

@Composable
private fun Detail(modifier: Modifier, text: String) {
   Box(modifier.background(Color.Green)) {
      Text(text)
   }
}
