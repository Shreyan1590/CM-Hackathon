package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.graphics.Color

private val LightColorScheme =
  lightColorScheme(
    primary = LifeLinePrimary,
    onPrimary = Color.White,
    primaryContainer = LifeLinePrimaryContainer,
    onPrimaryContainer = LifeLinePrimary,
    secondary = LifeLineSecondary,
    onSecondary = Color.White,
    secondaryContainer = LifeLineSecondaryContainer,
    onSecondaryContainer = LifeLineSecondary,
    background = LifeLineBackground,
    onBackground = LifeLineContentActive,
    surface = Color.White,
    onSurface = LifeLineContentActive,
    surfaceVariant = LifeLineSurface,
    onSurfaceVariant = LifeLineContentSecondary,
    outline = LifeLineBorder,
    error = LifeLineSOSRed,
    onError = Color.White,
    errorContainer = LifeLineSOSRedContainer,
    onErrorContainer = LifeLineSOSRed
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = false, // Force Light / White Theme for LifeLine Connect
  dynamicColor: Boolean = false, // Preserve our beautiful brand identity
  content: @Composable () -> Unit,
) {
  val colorScheme = LightColorScheme

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
