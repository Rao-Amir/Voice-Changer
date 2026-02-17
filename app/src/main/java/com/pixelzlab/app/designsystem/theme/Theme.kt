package com.pixelzlab.app.designsystem.theme

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

/**
 * App theme supporting Material 3, dynamic colors and custom design tokens
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    disableDynamicTheming: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        supportsDynamicTheming() && !disableDynamicTheming -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    // Custom design tokens
    val customColorsPalette = if (darkTheme) DarkCustomColors else LightCustomColors

    CompositionLocalProvider(LocalCustomColors provides customColorsPalette) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = content
        )
    }
}

/**
 * Preview wrapper for the AppTheme
 */
@Composable
fun AppThemePreview(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    AppTheme(
        darkTheme = darkTheme,
        disableDynamicTheming = true,
        content = content
    )
}

/**
 * Custom color palette for app-specific colors
 */
data class CustomColorsPalette(
    val warning: Color = Color.Unspecified,
    val success: Color = Color.Unspecified,
    val info: Color = Color.Unspecified,
    val divider: Color = Color.Unspecified,
    val uiBorder: Color = Color.Unspecified,
    val placeholder: Color = Color.Unspecified,
)

val LocalCustomColors = staticCompositionLocalOf { CustomColorsPalette() }

/**
 * Access custom colors through MaterialTheme
 */
val MaterialTheme.customColors: CustomColorsPalette
    @Composable
    @ReadOnlyComposable
    get() = LocalCustomColors.current

/**
 * Check if dynamic theming is supported (Android 12+)
 */
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun supportsDynamicTheming() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S