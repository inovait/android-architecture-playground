package si.inova.androidarchitectureplayground.navigation.scenes

import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import si.inova.androidarchitectureplayground.navigation.keys.base.TabScreenKey
import si.inova.kotlinova.navigation.navigation3.key
import si.inova.kotlinova.navigation.screenkeys.ScreenKey

class TabScreenScene(
   override val key: Any,
   override val previousEntries: List<NavEntry<ScreenKey>>,
   val tabEntry: NavEntry<ScreenKey>,
   val contentEntry: NavEntry<ScreenKey>,
) : Scene<ScreenKey> {

   override val content: @Composable (() -> Unit) = {
      CompositionLocalProvider(LocalTabSelection provides contentEntry) {
         tabEntry.Content()
      }
   }

   override val entries: List<NavEntry<ScreenKey>>
      get() = listOf(tabEntry, contentEntry)
}

val LocalTabSelection = staticCompositionLocalOf<NavEntry<ScreenKey>> { error("LocalTabSelection not provided") }

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun rememberTabScreenSceneStrategy(): TabScreenSceneStrategy {

   return remember {
      TabScreenSceneStrategy()
   }
}

class TabScreenSceneStrategy() : SceneStrategy<ScreenKey> {

   override fun SceneStrategyScope<ScreenKey>.calculateScene(entries: List<NavEntry<ScreenKey>>): Scene<ScreenKey>? {
      val tabScreenIndex = entries.indexOfLast { it.key() is TabScreenKey }.takeIf { it >= 0 } ?: return null
      val tabContentEntry = entries.elementAtOrNull(tabScreenIndex + 1) ?: return null

      val tabScreenEntry = entries.elementAt(tabScreenIndex)

      // We use the list's contentKey to uniquely identify the scene.
      // This allows the detail panes to be animated in and out by the scene, rather than
      // having NavDisplay animate the whole scene out when the selected detail item changes.
      val sceneKey = tabScreenEntry.contentKey

      return TabScreenScene(
         key = sceneKey,
         previousEntries = entries.dropLast(1),
         tabEntry = tabScreenEntry,
         contentEntry = tabContentEntry
      )
   }
}
