package si.inova.androidarchitectureplayground.navigation.base

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.SeekableTransitionState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.systemGestureExclusion
import androidx.compose.material3.VerticalDragHandle
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import androidx.window.layout.FoldingFeature
import com.google.accompanist.adaptive.SplitResult
import com.google.accompanist.adaptive.TwoPane
import com.google.accompanist.adaptive.TwoPaneStrategy
import com.google.accompanist.adaptive.calculateDisplayFeatures
import si.inova.androidarchitectureplayground.navigation.keys.base.DetailKey
import si.inova.androidarchitectureplayground.navigation.keys.base.ListKey
import si.inova.kotlinova.core.activity.requireActivity
import si.inova.kotlinova.navigation.navigation3.key
import si.inova.kotlinova.navigation.screenkeys.ScreenKey

class MasterDetailScene(
   override val key: Any,
   override val previousEntries: List<NavEntry<ScreenKey>>,
   val masterEntry: NavEntry<ScreenKey>,
   val detailEntry: NavEntry<ScreenKey>,
) : Scene<ScreenKey> {

   override val content: @Composable (() -> Unit) = {
      var offsetX by remember { mutableFloatStateOf(0f) }
      var screenWidth by remember { mutableIntStateOf(0) }
      val density = LocalDensity.current

      val displayFeatures = calculateDisplayFeatures(LocalContext.current.requireActivity())

      val verticalFold = displayFeatures.find {
         it is FoldingFeature
      } as FoldingFeature?

      val canSeparatorMove = verticalFold != null &&
         !verticalFold.isSeparating &&
         verticalFold.occlusionType != FoldingFeature.OcclusionType.FULL

      // When fold is in half opened mode, master should be at the bottom, so user can select things on the bottom
      // and watch detail on the top
      val flipMasterDetail = verticalFold != null && verticalFold.state == FoldingFeature.State.HALF_OPENED

      val detailPane: @Composable () -> Unit = {
         Row(Modifier.fillMaxHeight()) {
            if (canSeparatorMove) {
               VerticalDragHandle(
                  Modifier
                     .fillMaxHeight()
                     .wrapContentHeight()
                     .padding(16.dp)
                     .draggable(
                        orientation = Orientation.Horizontal,
                        state = rememberDraggableState { delta ->
                           with(density) {
                              offsetX =
                                 (offsetX + delta).coerceIn(
                                    MIN_PANE_WIDTH.toPx(),
                                    screenWidth - MIN_PANE_WIDTH.toPx(),
                                 )
                           }
                        },
                     )
                     .systemGestureExclusion() // To avoid colliding with the back gesture
               )
            }

            Crossfade(
               detailEntry,
               Modifier
                  .fillMaxHeight(),
               label = "Master Detail"
            ) { value ->
               Box(Modifier.fillMaxSize()) {
                  detailEntry.Content()
               }
            }
         }
      }

      val twoPaneStrategy = remember {
         TwoPaneStrategy {
               _,
               _,
               layoutCoordinates: LayoutCoordinates,
            ->

            SplitResult(
               gapOrientation = Orientation.Vertical,
               gapBounds = Rect(
                  left = offsetX,
                  top = 0f,
                  right = offsetX,
                  bottom = layoutCoordinates.size.height.toFloat(),
               )
            )
         }
      }

      TwoPane(
         if (flipMasterDetail) detailPane else masterEntry::Content,
         if (flipMasterDetail) masterEntry::Content else detailPane,
         twoPaneStrategy,
         displayFeatures = displayFeatures,
         modifier = Modifier
            .fillMaxSize()
            .layout
            { measurable, constraints ->
               val placeable = measurable.measure(constraints)
               if (screenWidth == 0) {
                  offsetX = placeable.width * DEFAULT_PANE_SPLIT
               }

               screenWidth = placeable.width

               layout(placeable.width, placeable.height) {
                  placeable.place(0, 0)
               }
            }
      )
   }
   override val entries: List<NavEntry<ScreenKey>>
      get() = listOf(masterEntry, detailEntry)
}

private suspend fun SeekableTransitionState<Boolean>.updateOpenState(targetState: Boolean, progress: Float?) {
   if (progress != null) {
      seekTo(progress, targetState)
   } else {
      if (targetState) {
         snapTo(targetState)
      } else {
         animateTo(targetState)
      }
   }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberMasterDetailSceneStrategy(): MasterDetailSceneStrategy {
   val windowSizeClass = calculateWindowSizeClass(LocalContext.current.requireActivity())

   return remember(windowSizeClass) {
      MasterDetailSceneStrategy(windowSizeClass)
   }
}

/**
 * A [SceneStrategy] that returns a [MasterDetailScene] if:
 *
 * - the window width is over 600dp
 * - A `Detail` entry is the last item in the back stack
 * - A `List` entry is in the back stack
 *
 * Notably, when the detail entry changes the scene's key does not change. This allows the scene,
 * rather than the NavDisplay, to handle animations when the detail entry changes.
 */
class MasterDetailSceneStrategy(val windowSizeClass: WindowSizeClass) : SceneStrategy<ScreenKey> {

   override fun SceneStrategyScope<ScreenKey>.calculateScene(entries: List<NavEntry<ScreenKey>>): Scene<ScreenKey>? {
      if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Companion.Compact) {
         return null
      }

      val detailEntry =
         entries.lastOrNull()?.takeIf { it.key() is DetailKey } ?: return null
      val masterEntry = entries.findLast { it.key() is ListKey } ?: return null

      // We use the list's contentKey to uniquely identify the scene.
      // This allows the detail panes to be animated in and out by the scene, rather than
      // having NavDisplay animate the whole scene out when the selected detail item changes.
      val sceneKey = masterEntry.contentKey

      return MasterDetailScene(
         key = sceneKey,
         previousEntries = entries.dropLast(1),
         masterEntry = masterEntry,
         detailEntry = detailEntry
      )
   }
}

private const val DEFAULT_PANE_SPLIT = 0.3f
private val MIN_PANE_WIDTH = 200.dp
