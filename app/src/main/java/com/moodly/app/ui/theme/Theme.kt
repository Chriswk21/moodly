package com.moodly.app.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val MoodlyColorScheme = lightColorScheme(
    primary = LavenderDeep,
    onPrimary = Color.White,
    primaryContainer = LavenderPrimary,
    onPrimaryContainer = TextDark,
    secondary = SoftBlue,
    onSecondary = TextDark,
    secondaryContainer = SoftBlue,
    onSecondaryContainer = TextDark,
    tertiary = StarYellow,
    onTertiary = TextDark,
    tertiaryContainer = BlushPink,
    onTertiaryContainer = TextDark,
    background = Background,
    onBackground = TextDark,
    surface = Surface,
    onSurface = TextDark,
    surfaceVariant = Background,
    onSurfaceVariant = TextMuted,
    outline = LavenderPrimary,
    outlineVariant = LavenderPrimary.copy(alpha = 0.5f),
    error = StressVeryHigh,
    onError = Color.White,
    errorContainer = StressVeryHigh.copy(alpha = 0.2f),
    onErrorContainer = TextDark
)

@Composable
fun MoodlyTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = true
                isAppearanceLightNavigationBars = true
            }
        }
    }

    MaterialTheme(
        colorScheme = MoodlyColorScheme,
        typography = MoodlyTypography,
        shapes = MoodlyShapes,
        content = content
    )
}
