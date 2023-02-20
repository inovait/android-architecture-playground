/*
 * Based on https://github.com/Zhuinden/simple-stack-compose-integration/blob/047febda99801993914556a76d8c2094b59be055/core/src/main/java/com/zhuinden/simplestackcomposeintegration/core/ComposeIntegrationCore.kt
 */
package si.inova.androidarchitectureplayground.simplestack

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.util.fastForEach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import com.zhuinden.simplestack.AsyncStateChanger
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger.Callback
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile
import si.inova.androidarchitectureplayground.navigation.base.Screen
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.SingleTopKey
import javax.inject.Provider

/**
 * A state changer that allows switching between composables, animating the transition.
 */
class ComposeStateChanger(
   private val screenFactories: Lazy<Map<@JvmSuppressWildcards Class<*>, @JvmSuppressWildcards Provider<Screen<*>>>>
) : AsyncStateChanger.NavigationHandler {
   private var currentStateChange by mutableStateOf<StateChangeData?>(null)
   private var lastCompletedCallback by mutableStateOf<Callback?>(null)

   override fun onNavigationEvent(stateChange: StateChange, completionCallback: Callback) {
      currentStateChange = StateChangeData(stateChange, completionCallback)
   }

   @OptIn(ExperimentalAnimationApi::class)
   @Composable
   fun Content(screenWrapper: @Composable (key: ScreenKey, screen: @Composable () -> Unit) -> Unit = { _, screen -> screen() }) {
      val saveableStateHolder = rememberSaveableStateHolder()

      CleanupStaleSavedStates(saveableStateHolder)

      val currentStateChange = currentStateChange
      val topKey = currentStateChange?.stateChange?.getNewKeys<ScreenKey>()?.lastOrNull() ?: return
      val stateChangeResult = StateChangeResult(currentStateChange.stateChange.direction, topKey)

      val transition = updateTransition(stateChangeResult, "AnimatedContent")
      transition.AnimatedContent(
         transitionSpec = {
            if (targetState.direction == StateChange.BACKWARD) {
               with(initialState.newTopKey) { backAnimation(this@AnimatedContent) }
            } else {
               with(targetState.newTopKey) { forwardAnimation(this@AnimatedContent) }
            }
         },
         contentKey = { it.newTopKey.contentKey() }
      ) { (_, topKey) ->
         TriggerCompletionCallback()

         saveableStateHolder.SaveableStateProvider(topKey.contentKey()) {
            LocalDestroyedLifecycle {
               screenWrapper(topKey) {
                  ShowScreen(topKey)
               }
            }
         }
      }
   }

   @Composable
   private fun ShowScreen(key: ScreenKey) {
      val screen = remember(key.contentKey()) {
         val screenClass = Class.forName(key.screenClass)
         val screenFactory = screenFactories.value[screenClass] ?: error(
            "Screen $screenClass is missing from factory map. " +
               "Did you specify screen's FQN properly?"
         )

         val screen = screenFactory.get()

         screen
      }

      @Suppress("UNCHECKED_CAST")
      (screen as Screen<ScreenKey>).Content(key)
   }

   @Composable
   private fun CleanupStaleSavedStates(saveableStateHolder: SaveableStateHolder) {
      LaunchedEffect(currentStateChange) {
         val stateChange = currentStateChange?.stateChange ?: return@LaunchedEffect
         val previousKeys = stateChange.getPreviousKeys<Any>()
         val newKeys = stateChange.getNewKeys<Any>()
         previousKeys.fastForEach { previousKey ->
            if (!newKeys.contains(previousKey)) {
               saveableStateHolder.removeState(previousKey)
            }
         }
      }
   }

   @Composable
   @OptIn(ExperimentalAnimationApi::class)
   private fun AnimatedVisibilityScope.TriggerCompletionCallback() {
      LaunchedEffect(currentStateChange) {
         val currentStateChange = currentStateChange ?: return@LaunchedEffect

         snapshotFlow { transition.totalDurationNanos to lastCompletedCallback }
            .takeWhile { (remainingAnimationDuration, lastCallback) ->
               if (remainingAnimationDuration == 0L && lastCallback != currentStateChange.completionCallback) {
                  lastCompletedCallback = currentStateChange.completionCallback
                  currentStateChange.completionCallback.stateChangeComplete()
                  return@takeWhile false
               }

               true
            }.collect()
      }
   }

   class StateChangeData(val stateChange: StateChange, val completionCallback: Callback)

   private fun ScreenKey.contentKey(): Any {
      return if (this is SingleTopKey && isSingleTop) {
         this.javaClass
      } else {
         this
      }
   }
}

/**
 * Wrapper that puts provided [child] into local lifecycle. Whenever this child is removed from
 * composition, its [LocalLifecycleOwner] will also get destroyed.
 */
@Composable
private fun LocalDestroyedLifecycle(child: @Composable () -> Unit) {
   val childLifecycleOwner = remember {
      object : LifecycleOwner {
         val lifecycle = LifecycleRegistry(this)
         override fun getLifecycle(): Lifecycle {
            return lifecycle
         }
      }
   }

   val childLifecycle = childLifecycleOwner.lifecycle

   val parentLifecycle = LocalLifecycleOwner.current.lifecycle

   DisposableEffect(parentLifecycle) {
      val parentListener = LifecycleEventObserver { _, event ->
         childLifecycle.handleLifecycleEvent(event)
      }

      parentLifecycle.addObserver(parentListener)

      onDispose {
         parentLifecycle.removeObserver(parentListener)

         if (childLifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            childLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
         }
      }
   }

   CompositionLocalProvider(LocalLifecycleOwner provides childLifecycleOwner) {
      child()
   }
}

/**
 * Composition local to access the Backstack within screens.
 */
val LocalBackstack =
   staticCompositionLocalOf<Backstack> {
      throw IllegalStateException(
         "You must ensure that the BackstackProvider provides the backstack, but it currently doesn't exist."
      )
   }

/**
 * Provider for the backstack composition local.
 */
@Composable
fun BackstackProvider(backstack: Backstack, content: @Composable () -> Unit) {
   CompositionLocalProvider(LocalBackstack provides (backstack)) {
      content()
   }
}
