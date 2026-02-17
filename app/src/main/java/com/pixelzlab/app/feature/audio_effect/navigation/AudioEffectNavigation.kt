package com.pixelzlab.app.feature.audio_effect.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.pixelzlab.app.feature.audio_effect.AudioEffectScreen

const val audioEffectRoute = "audio_effect_route"
const val audioFileArg = "audio_file"

fun NavController.navigateToApplyEffect(audioFilePath: String, navOptions: NavOptions? = null){
    val encodedPath = Uri.encode(audioFilePath)
    this.navigate("$audioEffectRoute/${encodedPath}", navOptions)
}

fun NavGraphBuilder.audioEffectScreen(navController: NavHostController){

    composable(route = "$audioEffectRoute/{$audioFileArg}"){ backStackEntry ->
        val encodedPath = backStackEntry.arguments?.getString(audioFileArg)
        val filePath = Uri.decode(encodedPath ?: "")
        AudioEffectScreen(
            navHostController = navController,
            audioFilePath = filePath
        )
    }
}