package com.pixelzlab.app.core.navigation

import androidx.navigation.NamedNavArgument
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.pixelzlab.app.model.entity.Pokemon

const val KeyId = "id"
const val KeyPokemon = "KeyPokemon"

sealed class AppDestination(val route: String = "") {

    open val arguments: List<NamedNavArgument> = emptyList()

    open var destination: String = route

    open var parcelableArgument: Pair<String, Any?> = "" to null

    object Splash: AppDestination("splash")

    object FragmentScreen : AppDestination("fragment_screen")

    object Up : AppDestination()

    object Home : AppDestination("home")

    object Detail: AppDestination("detail") {
        fun addParcel(value: Pokemon) = apply {
            parcelableArgument = KeyPokemon to value
        }
    }

    object Second : AppDestination("second/{$KeyId}") {

        override val arguments = listOf(
            navArgument(KeyId) { type = NavType.StringType }
        )

        fun createRoute(id: String) = apply {
            destination = "second/$id"
        }
    }
}
