package com.pixelzlab.app.feature.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.pixelzlab.app.feature.splash.SplashRoute

/**
 * Splash screen route
 */
const val splashRoute = "splash_route"

/**
 * Adds the splash screen to the navigation graph
 */
fun NavGraphBuilder.splashScreen(
    onSplashFinished: () -> Unit,
) {
    composable(route = splashRoute) {
        SplashRoute(navigator = { onSplashFinished() })
    }
} 