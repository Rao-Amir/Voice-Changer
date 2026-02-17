package com.pixelzlab.app.core.navigation

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pixelzlab.app.feature.detail.DetailRoute
import com.pixelzlab.app.feature.home.HomeRoute
import com.pixelzlab.app.feature.splash.SplashRoute
import com.pixelzlab.app.model.entity.Pokemon

/**
 * Created by pixelzlab on 01/03/2023.
 */

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Splash.destination,
        modifier = modifier
    ) {
        composable(AppDestination.Splash) {
            SplashRoute {
                navController.navigate(it)
            }
        }
        composable(AppDestination.Home) {
            HomeRoute(
                navigator = {
                    navController.navigate(it, it.parcelableArgument)
                }
            )
        }
        composable(AppDestination.Detail) {
            DetailRoute(
                pokemon = navController.previousBackStackEntry?.savedStateHandle?.get<Pokemon>(
                    KeyPokemon
                )
            )
        }
    }
}

private fun NavGraphBuilder.composable(
    destination: AppDestination,
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    composable(
        route = destination.route,
        arguments = destination.arguments,
        deepLinks = deepLinks,
        content = content
    )
}

/**
 * Navigate to provided [AppDestination] with a Pair of key value String and Data [parcel]
 * Caution to use this method. This method use savedStateHandle to store the Parcelable data.
 * When previousBackstackEntry is popped out from navigation stack, savedStateHandle will return null and cannot retrieve data.
 * eg.Login -> Home, the Login screen will be popped from the back-stack on logging in successfully.
 */
private fun NavHostController.navigate(
    appDestination: AppDestination,
    parcel: Pair<String, Any?>? = null
) {
    when (appDestination) {
        is AppDestination.Up -> navigateUp()
        else -> {
            parcel?.let { (key, value) ->
                currentBackStackEntry?.savedStateHandle?.set(key, value)
            }
            navigate(route = appDestination.destination)
        }
    }
}

