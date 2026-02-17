package com.pixelzlab.app.feature.detail.navigation

import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.pixelzlab.app.feature.detail.DetailRoute
import com.pixelzlab.app.model.entity.Pokemon
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.URLDecoder
import java.net.URLEncoder

/**
 * Detail screen route
 */
const val detailRoute = "detail_route"
const val pokemonIdArg = "pokemonId"

/**
 * Detail screen route with Pokemon object argument
 */
const val detailScreenRoute = "$detailRoute/{$pokemonIdArg}"

/**
 * Navigate to detail screen
 */
fun NavController.navigateToDetail(pokemon: Pokemon) {
    // In a production app, we might pass an ID instead and retrieve the full object from a repository
    // For this example, we'll use a JSON string as a parameter
    val encodedPokemon = URLEncoder.encode(Json.encodeToString(pokemon), "UTF-8")
    navigate("$detailRoute/$encodedPokemon")
}

/**
 * Class to retrieve the Pokemon argument from SavedStateHandle
 */
class DetailArgs(val pokemon: Pokemon) {
    companion object {
        fun fromSavedStateHandle(savedStateHandle: SavedStateHandle): DetailArgs {
            val encodedPokemon = checkNotNull(savedStateHandle[pokemonIdArg]) {
                "pokemonId parameter not found"
            }
            val decodedString = URLDecoder.decode(encodedPokemon.toString(), "UTF-8")
            val pokemon = Json.decodeFromString<Pokemon>(decodedString)
            return DetailArgs(pokemon)
        }
    }
}

/**
 * Adds the detail screen to the navigation graph
 */
fun NavGraphBuilder.detailScreen(
    onBackClick: () -> Unit,
) {
    composable(
        route = detailScreenRoute,
        arguments = listOf(
            navArgument(pokemonIdArg) {
                type = NavType.StringType
            }
        )
    ) {
        // In a real application, we would use DetailViewModel that consumes DetailArgs
        // For this example, we'll decode directly
        val encodedPokemon = it.arguments?.getString(pokemonIdArg) ?: ""
        val decodedString = URLDecoder.decode(encodedPokemon, "UTF-8")
        val pokemon = Json.decodeFromString<Pokemon>(decodedString)
        
        DetailRoute(pokemon = pokemon, onBackClick = onBackClick)
    }
} 