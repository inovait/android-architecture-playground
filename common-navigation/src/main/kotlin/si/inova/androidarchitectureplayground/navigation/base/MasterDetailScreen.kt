package si.inova.androidarchitectureplayground.navigation.base

import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.Crossfade
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.SeekableTransitionState
import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.TransitionState
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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
      val openState = rememberSaveable(
         saver = Saver(
            save = { it.currentState },
            restore = { SeekableTransitionState(it) }
         )
      ) { SeekableTransitionState(currentDetailScreen.value != null) }
      val lastKey = rememberSaveable { mutableStateOf(key) }
      val transition = rememberTransition(openState)

      val scope = rememberCoroutineScope()

      LaunchedEffect(defaultOpenDetails) {
         if (lastKey.value != key && defaultOpenDetails != currentDetailScreen.value) {
            currentDetailScreen.value = defaultOpenDetails
            openState.snapTo(defaultOpenDetails != null)
         }

         lastKey.value = key
      }

      fun openDetail(key: D) {
         currentDetailScreen.value = key
         scope.launch {
            openState.animateTo(true)
         }
      }

      val saveableStateHolder = rememberSaveableStateHolder()

      val master = remember {
         movableContentOf {
            saveableStateHolder.SaveableStateProvider(false) {
               Master(key, ::openDetail)
            }
         }
      }

      val detail = remember {
         movableContentOf<D> { detail ->
            if (detail != null) {
               saveableStateHolder.SaveableStateProvider(detail) {
                  Detail(detail)
               }
            }
         }
      }

      if (widthSize == WindowWidthSizeClass.Companion.Compact) {
         MasterDetailOnPhone(
            openState = openState,
            transition = transition,
            updateOpenState = openState::updateOpenState,
            currentDetailScreen = currentDetailScreen::value,
            master = master,
            detail = detail,
         )
      } else {
         MasterDetailOnLargerScreen(currentDetailScreen::value, master, detail)
      }
   }

   @Composable
   @OptIn(ExperimentalAnimationApi::class)
   private fun MasterDetailOnPhone(
      openState: TransitionState<Boolean>,
      transition: Transition<Boolean>,
      updateOpenState: suspend (Boolean, Float?) -> Unit,
      currentDetailScreen: () -> D?,
      master: @Composable () -> Unit,
      detail: @Composable (D) -> Unit,
   ) {
      transition.AnimatedContent(
         transitionSpec = {
            if (this.targetState) {
               slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left) togetherWith
                  slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
            } else {
               slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right) togetherWith
                  slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            }
         },
      ) { open ->
         if (open) {
            currentDetailScreen()?.let {
               Box(Modifier.fillMaxSize()) {
                  detail(it)
               }
            }
         } else {
            Box(Modifier.fillMaxSize()) {
               master()
            }
         }
      }

      PredictiveBackHandler(enabled = openState.currentState == true) { events ->
         var completed = false
         try {
            events.collectLatest {
               updateOpenState(false, it.progress)
            }
            completed = true
            updateOpenState(false, null)
         } catch (e: CancellationException) {
            if (!completed) {
               withContext(NonCancellable) {
                  updateOpenState(true, null)
               }
            }

            throw e
         }
      }
   }

   @Composable
   private fun MasterDetailOnLargerScreen(
      currentDetailScreen: () -> D?,
      master: @Composable () -> Unit,
      detail: @Composable (D) -> Unit,
   ) {
      Row(Modifier.fillMaxSize()) {
         Box(
            Modifier
               .weight(1f)
               .fillMaxHeight()
         ) {
            master()
         }

         Crossfade(
            currentDetailScreen(),
            Modifier
               .weight(2f)
               .fillMaxHeight(),
            label = "Master Detail"
         ) { value ->
            if (value != null) {
               Box(Modifier.fillMaxSize()) {
                  detail(value)
               }
            }
         }
      }
   }

   @Composable
   protected abstract fun Master(key: K, openDetail: (D) -> Unit)

   @Composable
   protected abstract fun Detail(key: D)
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
