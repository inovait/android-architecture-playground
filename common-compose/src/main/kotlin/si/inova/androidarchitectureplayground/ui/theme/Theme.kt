package si.inova.androidarchitectureplayground.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleAlpha
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
   primary = Purple80,
   secondary = PurpleGrey80,
   tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
   primary = Purple40,
   secondary = PurpleGrey40,
   tertiary = Pink40

   /*
      Other default colors to override
      background = Color(0xFFFFFBFE),
      surface = Color(0xFFFFFBFE),
      onPrimary = Color.White,
      onSecondary = Color.White,
      onTertiary = Color.White,
      onBackground = Color(0xFF1C1B1F),
      onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun AndroidArchitecturePlaygroundTheme(
   darkTheme: Boolean = isSystemInDarkTheme(),
   // Dynamic color is available on Android 12+
   dynamicColor: Boolean = true,
   content: @Composable () -> Unit,
) {
   val colorScheme = when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
         val context = LocalContext.current
         if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
   }

   MaterialTheme(
      colorScheme = colorScheme,
      typography = MyTypography,
   ) {
      val defaultRippleTheme = LocalRippleTheme.current
      if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
         // Workaround for https://issuetracker.google.com/issues/274471576 - Ripple effects do not work on Android 13
         CompositionLocalProvider(LocalRippleTheme provides TransparencyFixRippleTheme(defaultRippleTheme)) {
            content()
         }
      } else {
         content()
      }
   }
}

@Stable
private class TransparencyFixRippleTheme(private val defaultTheme: RippleTheme) : RippleTheme {
   @Composable
   override fun defaultColor() = defaultTheme.defaultColor()

   @Composable
   override fun rippleAlpha() = defaultTheme.rippleAlpha().run {
      RippleAlpha(
         draggedAlpha,
         focusedAlpha,
         hoveredAlpha,
         pressedAlpha.coerceAtLeast(MIN_RIPPLE_ALPHA_ON_TIRAMISU)
      )
   }
}

private const val MIN_RIPPLE_ALPHA_ON_TIRAMISU = 0.5f
