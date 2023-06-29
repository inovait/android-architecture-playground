package si.inova.androidarchitectureplayground.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import si.inova.kotlinova.core.activity.requireActivity
import si.inova.kotlinova.navigation.screenkeys.ScreenKey
import si.inova.kotlinova.navigation.screens.Screen

abstract class MasterDetailScreen<K : ScreenKey, D> : Screen<K>() {
   protected open fun getDefaultOpenDetails(key: K): D? {
      return null
   }

   @Composable
   @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
   override fun Content(key: K) {
      val windowSize = calculateWindowSizeClass(LocalContext.current.requireActivity())
      MasterDetail(key, windowSize.widthSizeClass)
   }

   @Composable
   private fun MasterDetail(key: K, widthSize: WindowWidthSizeClass) {
      val defaultOpenDetails = getDefaultOpenDetails(key)

      val currentDetailScreen = rememberSaveable { mutableStateOf<D?>(defaultOpenDetails) }
      val openState = rememberSaveable { mutableStateOf(currentDetailScreen.value != null) }
      val lastKey = rememberSaveable { mutableStateOf(key) }

      LaunchedEffect(defaultOpenDetails) {
         if (lastKey.value != key && defaultOpenDetails != currentDetailScreen.value) {
            currentDetailScreen.value = defaultOpenDetails
            openState.value = defaultOpenDetails != null
         }

         lastKey.value = key
      }

      fun openDetail(key: D) {
         currentDetailScreen.value = key
         openState.value = true
      }

      val master = remember {
         movableContentOf<Modifier> { modifier ->
            Box(modifier) {
               Master(key, ::openDetail)
            }
         }
      }

      val detail = remember {
         movableContentOf<Modifier, D> { _, detail ->
            if (detail != null) {
               Detail(detail)
            }
         }
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
      currentDetailScreen: MutableState<D?>,
      master: @Composable (Modifier) -> Unit,
      detail: @Composable (Modifier, D) -> Unit
   ) {
      val saveableStateHolder = rememberSaveableStateHolder()

      AnimatedContent(
         openState.value,
         transitionSpec = {
            if (this.targetState) {
               slideIntoContainer(AnimatedContentScope.SlideDirection.Left) with
                  slideOutOfContainer(AnimatedContentScope.SlideDirection.Left)
            } else {
               slideIntoContainer(AnimatedContentScope.SlideDirection.Right) with
                  slideOutOfContainer(AnimatedContentScope.SlideDirection.Right)
            }
         },
         label = "Master Detail"
      ) { open ->
         saveableStateHolder.SaveableStateProvider(open) {
            if (open) {
               currentDetailScreen.value?.let {
                  detail(Modifier.fillMaxSize(), it)
               }
            } else {
               master(Modifier.fillMaxSize())
            }
         }
      }

      BackHandler(enabled = openState.value) {
         openState.value = false
      }
   }

   @Composable
   private fun MasterDetailOnLargerScreen(
      currentDetailScreen: MutableState<D?>,
      master: @Composable (Modifier) -> Unit,
      detail: @Composable (Modifier, D) -> Unit
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
               .fillMaxHeight(),
            label = "Master Detail"
         ) { value ->
            if (value != null) {
               detail(Modifier.fillMaxSize(), value)
            }
         }
      }
   }

   @Composable
   protected abstract fun Master(key: K, openDetail: (D) -> Unit)

   @Composable
   protected abstract fun Detail(key: D)
}
