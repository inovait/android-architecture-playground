/*
 * Based on https://github.com/Zhuinden/simple-stack-compose-integration/blob/047febda99801993914556a76d8c2094b59be055/core/src/main/java/com/zhuinden/simplestackcomposeintegration/core/ComposeIntegrationCore.kt
 */
package si.inova.androidarchitectureplayground.simplestack

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.MeasurePolicy
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMaxBy
import com.zhuinden.simplestack.AsyncStateChanger
import com.zhuinden.simplestack.Backstack
import com.zhuinden.simplestack.StateChange
import com.zhuinden.simplestack.StateChanger
import kotlinx.coroutines.launch
import si.inova.androidarchitectureplayground.screens.Screen
import si.inova.androidarchitectureplayground.screens.ScreenKey
import si.inova.androidarchitectureplayground.simplestack.ComposeStateChanger.AnimationConfiguration.ComposableAnimationSpec
import si.inova.androidarchitectureplayground.simplestack.ComposeStateChanger.AnimationConfiguration.ComposableTransition
import javax.inject.Provider

/**
 * A state changer that allows switching between composables, animating the transition.
 */
class ComposeStateChanger(
   private val animationConfiguration: AnimationConfiguration = AnimationConfiguration(),
   private val screenFactories: Lazy<Map<@JvmSuppressWildcards Class<*>, @JvmSuppressWildcards Provider<Screen<*>>>>
) : AsyncStateChanger.NavigationHandler {
   private var backstackState by mutableStateOf(
      BackstackState(
         animationConfiguration = animationConfiguration,
         screenFactories = screenFactories
      )
   )

   override fun onNavigationEvent(
      stateChange: StateChange,
      completionCallback: StateChanger.Callback
   ) {
      this.backstackState = BackstackState(
         animationConfiguration = animationConfiguration,
         stateChange = stateChange,
         callback = completionCallback,
         screenFactories = screenFactories
      )
   }

   /**
    * Configuration for the screen switching animations.
    */
   class AnimationConfiguration(
      /**
       * The previous transition.
       */
      @Suppress("UNUSED_ANONYMOUS_PARAMETER")
      val previousComposableTransition: ComposableTransition =
         ComposableTransition { modifier, stateChange, fullWidth, fullHeight, animationProgress ->
            modifier.then(
               when (stateChange.direction) {
                  StateChange.FORWARD -> Modifier.graphicsLayer(translationX = 0 + (-1) * fullWidth * animationProgress)
                  StateChange.BACKWARD -> Modifier.graphicsLayer(translationX = 0 + fullWidth * animationProgress)
                  else /* REPLACE */ -> Modifier.graphicsLayer(alpha = (1 - animationProgress))
               }
            )
         },
      /**
       * The new transition.
       */
      @Suppress("UNUSED_ANONYMOUS_PARAMETER")
      val newComposableTransition: ComposableTransition =
         ComposableTransition { modifier, stateChange, fullWidth, fullHeight, animationProgress ->
            modifier.then(
               when (stateChange.direction) {
                  StateChange.FORWARD -> Modifier.graphicsLayer(translationX = fullWidth + (-1) * fullWidth * animationProgress)
                  StateChange.BACKWARD -> Modifier.graphicsLayer(translationX = -1 * fullWidth + fullWidth * animationProgress)
                  else /* REPLACE */ -> Modifier.graphicsLayer(alpha = 0 + animationProgress)
               }
            )
         },
      /**
       * The animation spec.
       */
      @Suppress("UNUSED_ANONYMOUS_PARAMETER")
      val animationSpec: ComposableAnimationSpec = ComposableAnimationSpec { stateChange ->
         TweenSpec(250, 0, LinearEasing)
      },
      /**
       * An optional composable content wrapper.
       */
      val contentWrapper: ComposableContentWrapper = object : ComposableContentWrapper {
         @Composable
         override fun ContentWrapper(stateChange: StateChange, block: @Composable() () -> Unit) {
            block()
         }
      }
   ) {
      /**
       * An interface to describe transition of a composables.
       */
      fun interface ComposableTransition {
         @SuppressLint("ModifierFactoryExtensionFunction")
         fun animateComposable(
            modifier: Modifier,
            stateChange: StateChange,
            fullWidth: Int,
            fullHeight: Int,
            animationProgress: Float
         ): Modifier
      }

      /**
       * An interface to describe animation spec of transitions.
       */
      fun interface ComposableAnimationSpec {
         fun defineAnimationSpec(stateChange: StateChange): AnimationSpec<Float>
      }

      /**
       * An interface to describe an optional content wrapper for the animated content.
       */
      interface ComposableContentWrapper {
         @Composable
         fun ContentWrapper(stateChange: StateChange, block: @Composable() () -> Unit)
      }
   }

   private data class BackstackState(
      private val animationConfiguration: AnimationConfiguration,
      private val stateChange: StateChange? = null,
      private val callback: StateChanger.Callback? = null,
      private val screenFactories: Lazy<Map<@JvmSuppressWildcards Class<*>, @JvmSuppressWildcards Provider<Screen<*>>>>
   ) {
      @Composable
      fun RenderScreen(modifier: Modifier = Modifier) {
         val stateChange = stateChange ?: return
         val callback = callback ?: return

         val saveableStateHolder = rememberSaveableStateHolder()

         var completionCallback by remember { mutableStateOf<StateChanger.Callback?>(null) }

         val topNewKey by rememberUpdatedState(newValue = stateChange.topNewKey<ScreenKey>())
         val topPreviousKey by rememberUpdatedState(newValue = stateChange.topPreviousKey<ScreenKey>())

         var isAnimating by remember { mutableStateOf(false) }

         val lerping = remember { Animatable(0.0f) }

         var animationProgress by remember { mutableStateOf(0.0f) }

         var initialization by remember { mutableStateOf(true) }

         if (completionCallback !== callback) {
            completionCallback = callback

            if (topPreviousKey != null) {
               initialization = false

               animationProgress = 0.0f
               isAnimating = true
            } else {
               initialization = true
            }
         }

         var fullWidth by remember { mutableStateOf(0) }
         var fullHeight by remember { mutableStateOf(0) }

         val measurePolicy = MeasurePolicy { measurables, constraints ->
            val placeables = measurables.fastMap { it.measure(constraints) }
            val maxWidth = placeables.fastMaxBy { it.width }?.width ?: 0
            val maxHeight = placeables.fastMaxBy { it.height }?.height ?: 0

            if (fullWidth == 0 && maxWidth != 0) {
               fullWidth = maxWidth
            }

            if (fullHeight == 0 && maxHeight != 0) {
               fullHeight = maxHeight
            }

            layout(maxWidth, maxHeight) {
               placeables.fastForEach { placeable ->
                  placeable.place(0, 0)
               }
            }
         }

         val previousTransition = animationConfiguration.previousComposableTransition
         val newTransition = animationConfiguration.newComposableTransition

         val contentWrapper = animationConfiguration.contentWrapper

         var initialNewKey by remember { mutableStateOf(topNewKey) }

         val newKeys by rememberUpdatedState(newValue = stateChange.getNewKeys<ScreenKey>())
         val previousKeys by rememberUpdatedState(newValue = stateChange.getPreviousKeys<ScreenKey>())

         val allKeys by rememberUpdatedState(newValue = mutableListOf<ScreenKey>().apply {
            addAll(newKeys)

            previousKeys.fastForEach { previousKey ->
               if (!newKeys.contains(previousKey)) {
                  add(0, previousKey)
               }
            }
         }.toList())

         Layout(
            content = {
               allKeys.fastForEach { key ->
                  key(key) {
                     contentWrapper.ContentWrapper(stateChange = stateChange) {
                        if (key == topNewKey || (isAnimating && key == initialNewKey)) {
                           saveableStateHolder.SaveableStateProvider(key = key) {
                              Box(
                                 modifier = when {
                                    !isAnimating || initialization -> modifier
                                    else -> when {
                                       key == topNewKey -> newTransition.animateComposable(
                                          modifier,
                                          stateChange,
                                          fullWidth,
                                          fullHeight,
                                          animationProgress
                                       )

                                       else -> previousTransition.animateComposable(
                                          modifier,
                                          stateChange,
                                          fullWidth,
                                          fullHeight,
                                          animationProgress
                                       )
                                    }
                                 }
                              ) {
                                 val screen = remember(key) {
                                    val screenClass = Class.forName(key.screenClass)
                                    val screenFactory = screenFactories.value[screenClass]
                                       ?: throw IllegalStateException(
                                          "Screen $screenClass is missing from factory map. " +
                                             "Did you specify screen's FQN properly?"
                                       )

                                    val screen = screenFactory.get()

                                    @Suppress("UNCHECKED_CAST")
                                    (screen as Screen<ScreenKey>).key = key

                                    screen
                                 }

                                 screen.Content()
                              }
                           }
                        }
                     }
                  }
               }
            },
            measurePolicy = measurePolicy,
         )

         val coroutineScope = rememberCoroutineScope()

         DisposableEffect(key1 = completionCallback, effect = {
            @Suppress("NAME_SHADOWING")
            val topNewKey = topNewKey // ensure this is kept while the animation is progressing, I guess?

            @Suppress("NAME_SHADOWING")
            val completionCallback = completionCallback  // ensure this is kept while the animation is progressing, I guess?

            val job = coroutineScope.launch {
               if (isAnimating) {
                  lerping.animateTo(1.0f, animationConfiguration.animationSpec.defineAnimationSpec(stateChange = stateChange)) {
                     animationProgress = this.value
                  }
                  isAnimating = false
                  lerping.snapTo(0f)
               }
               initialNewKey = topNewKey

               previousKeys.fastForEach { previousKey ->
                  if (!newKeys.contains(previousKey)) {
                     saveableStateHolder.removeState(previousKey)
                  }
               }

               try {
                  completionCallback!!.stateChangeComplete()
               } catch (e: IllegalStateException) {
                  Log.i("ComposeStateChanger", "Unexpected double call to completion callback", e)
               }
            }

            onDispose {
               try {
                  if (!job.isCompleted) {
                     job.cancel()
                  }
               } catch (e: Throwable) {
                  // I don't think this can happen, but even if it did, it wouldn't be useful here.
                  // Having to cancel the job would only happen if the animation is in progress while the composable is removed.
               }
            }
         })
      }
   }

   @Composable
   fun RenderScreen(modifier: Modifier = Modifier) {
      LocalBackstack.current // force `BackstackProvider` to be set

      backstackState.RenderScreen(modifier)
   }
}

/**
 * Composition local to access the Backstack within screens.
 */
val LocalBackstack =
   staticCompositionLocalOf<Backstack> { throw IllegalStateException("You must ensure that the BackstackProvider provides the backstack, but it currently doesn't exist.") }

/**
 * Provider for the backstack composition local.
 */
@Composable
fun BackstackProvider(backstack: Backstack, content: @Composable() () -> Unit) {
   CompositionLocalProvider(LocalBackstack provides (backstack)) {
      content()
   }
}
