package com.pixelzlab.app.feature.voice_recorder.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.pixelzlab.app.feature.voice_recorder.VoiceRecorderScreen

const val voiceRecorderRoute = "voice_recorder_route"

fun NavController.navigateToVoiceRecorder(navOptions: NavOptions? = null) {
    this.navigate(voiceRecorderRoute, navOptions)
}

fun NavGraphBuilder.voiceRecorderScreen(navController: NavHostController) {
    composable(route = voiceRecorderRoute) {
        VoiceRecorderScreen(
            navController = navController
       )
    }
}