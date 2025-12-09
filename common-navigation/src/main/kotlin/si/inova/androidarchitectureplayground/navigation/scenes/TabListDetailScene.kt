package si.inova.androidarchitectureplayground.navigation.scenes

import androidx.compose.animation.Crossfade
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
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
import si.inova.androidarchitectureplayground.navigation.keys.base.TabContainerKey
import si.inova.kotlinova.core.activity.requireActivity
import si.inova.kotlinova.navigation.navigation3.key
import si.inova.kotlinova.navigation.screenkeys.ScreenKey

/**
 * Combined scene that handles both tab layout and list-detail
 *
 * Since Navigation3 does not support nested scenes, we were forced to merge them all into one big scene
 */
class TabListDetailScene(
   override val key: Any,
   override val previousEntries: List<NavEntry<ScreenKey>>,
   val tabContainerEntry: NavEntry<ScreenKey>?,
   val listEntry: NavEntry<ScreenKey>?,
   val detailEntry: NavEntry<ScreenKey>?,
   val showListDetail: Boolean,
) : Scene<ScreenKey> {

   override val content: @Composable (() -> Unit) = {
      val mainContent: @Composable (() -> Unit) = {
         if (listEntry != null) {
            if (showListDetail) {
               ListDetail(listEntry, detailEntry)
            } else {
               listEntry.Content()
            }
         } else {
            detailEntry?.Content() ?: error("Detail entry should not be null when there is no list present")
         }
      }

      if (tabContainerEntry != null) {
         val selectedTabContent = SelectedTabContent(mainContent, listEntry?.key() ?: detailEntry!!.key())
         CompositionLocalProvider(LocalSelectedTabContent provides selectedTabContent) {
            tabContainerEntry.Content()
         }
      } else {
         detailEntry?.Content() ?: error("Detail entry should not be null when there is no list present")
      }
   }

   override val entries: List<NavEntry<ScreenKey>>
      get() = listOfNotNull(tabContainerEntry, listEntry, detailEntry)
}

@Composable
private fun ListDetail(
   listEntry: NavEntry<ScreenKey>,
   detailEntry: NavEntry<ScreenKey>?,
) {
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
   val flipListDetail = verticalFold != null && verticalFold.state == FoldingFeature.State.HALF_OPENED

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
            label = "List Detail"
         ) { value ->
            Box(Modifier.fillMaxSize()) {
               value?.Content()
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
      if (flipListDetail) detailPane else listEntry::Content,
      if (flipListDetail) listEntry::Content else detailPane,
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

val LocalSelectedTabContent = staticCompositionLocalOf<SelectedTabContent> { error("SelectedTabContent not provided") }

data class SelectedTabContent(val content: @Composable () -> Unit, val key: ScreenKey)

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberTabListDetailSceneStrategy(): TabListDetailSceneStrategy {
   val windowSizeClass = calculateWindowSizeClass(LocalContext.current.requireActivity())

   return remember(windowSizeClass) {
      TabListDetailSceneStrategy(windowSizeClass)
   }
}

class TabListDetailSceneStrategy(val windowSizeClass: WindowSizeClass) : SceneStrategy<ScreenKey> {

   @Suppress("CyclomaticComplexMethod") // Splitting up would make things even worse. Method is commented to compensate.
   override fun SceneStrategyScope<ScreenKey>.calculateScene(entries: List<NavEntry<ScreenKey>>): Scene<ScreenKey>? {
      val largeDevice: Boolean = windowSizeClass.widthSizeClass != WindowWidthSizeClass.Companion.Compact

      // Possible configurations (when those items are at the top of the backstack):
      // Phone + [TabContainer, List] => We should show both
      // Tablet + [TabContainer, List] => We should show both
      // Phone + [TabContainer, Non-List] => We should show both
      // Tablet + [TabContainer, Non-List] => We should show both
      // Phone + [TabContainer, List, Detail] => We should only show Detail
      // Tablet + [TabContainer, List, Detail] => We should show all three
      // Phone + [List, Detail] => We should only show Detail
      // Tablet + [List, Detail] => We should show both
      // Phone + [List, Non-Detail] => We should only show Detail
      // Tablet + [List, Non-Detail] => We should only show Detail
      // No list or TabContainer => Ignore (return null)

      val tabContainerEntryIndex = entries.indexOfLast { it.key() is TabContainerKey }.takeIf { it >= 0 }
      // List is only relevant to us if it is at the top or the second top item of the backstack
      val listEntryIndex = entries.indexOfLast { it.key() is ListKey }.takeIf { it >= 0 && it >= entries.lastIndex - 1 }
      val detailEntry = entries.lastOrNull() ?: return null

      val listAndDetailsPresent = listEntryIndex != entries.lastIndex

      if (!largeDevice && listEntryIndex != null && listAndDetailsPresent) {
         // Phone + Details cases. Disable this scene strategy, we must show details on the phone
         return null
      }

      if (listEntryIndex != null && listAndDetailsPresent && detailEntry.key() !is DetailKey) {
         // We have a list, but the top entry on the backstack is not a details entry. Show it fullscreen.
         return null
      }

      // Tab is only relevant to us if it's either at the top of the backstack, behind the list or second to top (without list)
      val filteredTabContainerScreenEntryIndex = tabContainerEntryIndex.takeIf {
         (listEntryIndex == null && tabContainerEntryIndex == entries.lastIndex - 1) ||
            (listEntryIndex != null && tabContainerEntryIndex == listEntryIndex - 1)
      }

      if (filteredTabContainerScreenEntryIndex == null && (listEntryIndex == null || !largeDevice)) {
         // No tabs - only enable when we are on a tablet AND there is a list present
         return null
      }

      val tabContainerEntry = filteredTabContainerScreenEntryIndex?.let { entries.elementAt(it) }
      val listEntry = listEntryIndex?.let { entries.elementAt(it) }

      return TabListDetailScene(
         key = "tab-list-detail",
         previousEntries = entries.takeWhile { it != tabContainerEntry && it != listEntry },
         tabContainerEntry = tabContainerEntry,
         listEntry = listEntry,
         // Detail can be null only when there is a list shown, but user has not selected any detail yet
         detailEntry = detailEntry.takeIf { listAndDetailsPresent },
         showListDetail = largeDevice,
      )
   }
}

private const val DEFAULT_PANE_SPLIT = 0.3f
private val MIN_PANE_WIDTH = 200.dp
