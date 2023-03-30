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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.zhuinden.simplestack.AsyncStateChanger
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger.Callback
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.takeWhile
import si.inova.androidarchitectureplayground.di.NavigationInjection
import si.inova.androidarchitectureplayground.navigation.keys.ScreenKey
import si.inova.androidarchitectureplayground.navigation.keys.SingleTopKey

/**
 * A state changer that allows switching between composables, animating the transition.
 */
class ComposeStateChanger : AsyncStateChanger.NavigationHandler {
   private var currentStateChange by mutableStateOf<StateChangeData?>(null)
   private var lastCompletedCallback by mutableStateOf<Callback?>(null)

   private lateinit var screenRegistry: ScreenRegistry

   override fun onNavigationEvent(stateChange: StateChange, completionCallback: Callback) {
      if (!::screenRegistry.isInitialized) {
         screenRegistry = NavigationInjection.fromBackstack(stateChange.backstack).screenRegistry()
      }
      currentStateChange = StateChangeData(stateChange, completionCallback)
   }

   @OptIn(ExperimentalAnimationApi::class)
   @Composable
   fun Content(screenWrapper: @Composable (key: ScreenKey, screen: @Composable () -> Unit) -> Unit = { _, screen -> screen() }) {
      val saveableStateHolder = rememberSaveableStateHolder()
      val viewModelStores = viewModel<StoreHolderViewModel>()

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
         TriggerCompletionCallback(saveableStateHolder, viewModelStores)
         val contentKey = topKey.contentKey()

         saveableStateHolder.SaveableStateProvider(contentKey) {
            viewModelStores.WithLocalViewModelStore(contentKey) {
               LocalDestroyedLifecycle {
                  screenWrapper(topKey) {
                     ShowScreen(topKey)
                  }
               }
            }
         }
      }
   }

   @Composable
   private fun ShowScreen(key: ScreenKey) {
      val screen = remember(key.contentKey()) {
         screenRegistry.createScreen(key)
      }

      screen.Content(key)
   }

   private fun cleanupStaleSaveStates(
      currentStateChange: StateChangeData,
      saveableStateHolder: SaveableStateHolder,
      viewModelStores: StoreHolderViewModel
   ) {
      val stateChange = currentStateChange.stateChange
      val previousKeys = stateChange.getPreviousKeys<Any>()
      val newKeys = stateChange.getNewKeys<Any>()
      previousKeys.fastForEach { previousKey ->
         if (!newKeys.contains(previousKey)) {
            saveableStateHolder.removeState(previousKey)
            viewModelStores.removeKey(previousKey)
         }
      }
   }

   @Composable
   @OptIn(ExperimentalAnimationApi::class)
   private fun AnimatedVisibilityScope.TriggerCompletionCallback(
      saveableStateHolder: SaveableStateHolder,
      viewModelStores: StoreHolderViewModel
   ) {
      LaunchedEffect(currentStateChange) {
         val currentStateChange = currentStateChange ?: return@LaunchedEffect

         snapshotFlow { transition.totalDurationNanos to lastCompletedCallback }
            .takeWhile { (remainingAnimationDuration, lastCallback) ->
               if (remainingAnimationDuration == 0L && lastCallback != currentStateChange.completionCallback) {
                  lastCompletedCallback = currentStateChange.completionCallback
                  currentStateChange.completionCallback.stateChangeComplete()

                  cleanupStaleSaveStates(currentStateChange, saveableStateHolder, viewModelStores)

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
         override val lifecycle = LifecycleRegistry(this)
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

class StoreHolderViewModel : ViewModel() {
   private val viewModelStores = HashMap<Any, ViewModelStoreOwner>()

   fun removeKey(key: Any) {
      viewModelStores.remove(key)?.viewModelStore?.clear()
   }

   @Composable
   fun WithLocalViewModelStore(key: Any, block: @Composable () -> Unit) {
      val storeOwner = viewModelStores.getOrPut(key) {
         object : ViewModelStoreOwner {
            override val viewModelStore: ViewModelStore = ViewModelStore()
         }
      }

      CompositionLocalProvider(LocalViewModelStoreOwner provides storeOwner) {
         block()
      }
   }

   override fun onCleared() {
      for (store in viewModelStores.values) {
         store.viewModelStore.clear()
      }
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
