package com.pixelzlab.app.feature.home.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.pixelzlab.app.feature.home.HomeRoute
import com.pixelzlab.app.model.entity.Pokemon

/**
 * Home screen route
 */
const val homeRoute = "home_route"

/**
 * Navigate to home screen
 */
fun NavController.navigateToHome(navOptions: NavOptions? = null) {
    this.navigate(homeRoute, navOptions)
}

/**
 * Adds the home screen to the navigation graph
 */
fun NavGraphBuilder.homeScreen(
    onPokemonClick: (Pokemon) -> Unit,
) {
    composable(route = homeRoute) {
        HomeRoute(
            navigator = { destination ->
                // This will be replaced with direct function calls in the new approach
                if (destination is com.pixelzlab.app.core.navigation.AppDestination.Detail) {
                    val pokemon = destination.parcelableArgument.second as Pokemon
                    onPokemonClick(pokemon)
                }
            }
        )
    }
} 