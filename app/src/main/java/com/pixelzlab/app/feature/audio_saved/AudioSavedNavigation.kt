package com.pixelzlab.app.feature.audio_saved

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val audioEffectRoute = "audio_saved_route"
const val savedFileArg = "saved_file"

fun NavController.navigateAudioSaved(filePath: String, navOptions: NavOptions? = null) {
    val encodedPath = Uri.encode(filePath)
    this.navigate("$audioEffectRoute/$encodedPath", navOptions)
}

fun NavGraphBuilder.audioSavedScreen(navController: NavHostController) {
    composable(route = "$audioEffectRoute/{$savedFileArg}") { backStackEntry ->
        val filePath = Uri.decode(backStackEntry.arguments?.getString(savedFileArg) ?: "")
        AudioSavedScreen(navController, savedFilePath = filePath)
    }
}