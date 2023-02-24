package si.inova.androidarchitectureplayground.screens

import androidx.annotation.RawRes
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import app.rive.runtime.kotlin.RiveAnimationView
import kotlinx.coroutines.launch
import si.inova.androidarchitectureplayground.R
import si.inova.androidarchitectureplayground.navigation.Navigator
import si.inova.androidarchitectureplayground.navigation.base.Screen
import si.inova.androidarchitectureplayground.navigation.keys.ScreenAKey
import kotlin.math.roundToInt

@Suppress("unused")
class ScreenA constructor(
   private val viewModel: ScreenAViewModel,
   private val navigator: Navigator
) : Screen<ScreenAKey>() {
   @Composable
   override fun Content(key: ScreenAKey) {
      val animations = listOf(
         AnimationFromTeam("Animation 1", R.raw.animation_1),
         AnimationFromTeam("Animation 2", R.raw.animation_2)
      )

      Column {
         val scope = rememberCoroutineScope()

         var sliderValue by remember { mutableStateOf(50f) }
         val animatable = remember { Animatable(0f) }
         var lock by remember { mutableStateOf(false) }
         var selectedAnimationIndex by remember { mutableStateOf(0) }

         Row(
            Modifier
               .horizontalScroll(rememberScrollState())
               .padding(32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
         ) {
            animations.forEachIndexed { index, animation ->
               Button(
                  onClick = {
                     selectedAnimationIndex = index
                  },
                  enabled = index != selectedAnimationIndex
               ) {
                  Text(animation.name)
               }
            }
         }

         Slider(
            sliderValue,
            onValueChange = { sliderValue = it.also { println("target $it") } },
            valueRange = 0f..100f,
            modifier = Modifier.padding(start = 32.dp, end = 32.dp)
         )

         Row(
            Modifier.padding(32.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
         ) {
            Text(sliderValue.roundToInt().toString(), fontSize = 20.sp, modifier = Modifier.width(40.dp))

            Button(onClick = {
               scope.launch {
                  animatable.snapTo(sliderValue)
               }
            }) {
               Text("Snap")
            }

            Button(onClick = {
               scope.launch {
                  animatable.animateTo(sliderValue)
               }
            }) {
               Text("Animate")
            }

            Checkbox(lock, onCheckedChange = { lock = it })
            Text("Lock to slider", fontSize = 16.sp)
         }

         LaunchedEffect(lock, sliderValue) {
            if (lock) {
               animatable.snapTo(sliderValue)
            }
         }

         LevelAnimation(animations[selectedAnimationIndex].res, animatable.value)
      }
   }

   @Composable
   private fun LevelAnimation(
      @RawRes
      animationRes: Int,
      level: Float,
      modifier: Modifier = Modifier
   ) {
      AndroidView(
         {
            RiveAnimationView(it)
         }, update = {
            if (it.tag != animationRes) {
               it.tag = animationRes
               it.setRiveResource(animationRes, stateMachineName = "State Machine")
            }

            for (machine in it.stateMachines) {
               for (input in machine.inputs) {
                  if (input.isNumber) {
                     it.setNumberState(machine.name, input.name, level)
                  }
               }
            }
         },
         modifier = modifier
      )
   }
}

data class AnimationFromTeam(
   val name: String,
   @RawRes
   val res: Int
)
