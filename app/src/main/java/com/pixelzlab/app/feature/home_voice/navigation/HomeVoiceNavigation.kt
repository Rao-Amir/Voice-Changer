package com.pixelzlab.app.feature.home_voice.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.pixelzlab.app.feature.audio_import.navigation.navigateToAudioImport
import com.pixelzlab.app.feature.audio_list.navigation.navigateToAudioList
import com.pixelzlab.app.feature.home_voice.HomeVoiceScreen
import com.pixelzlab.app.feature.text_to_speech.navigation.navigateTextToSpeech
import com.pixelzlab.app.feature.voice_recorder.MainScaffoldWithDrawer
import com.pixelzlab.app.feature.voice_recorder.navigation.navigateToVoiceRecorder

const val homeVoiceRoute = "home_voice_route"

fun NavController.navigateToHomeVoice(navOptions: NavOptions? = null) {
    this.navigate(homeVoiceRoute, navOptions)
}

fun NavGraphBuilder.homeVoiceScreen(navController: NavHostController) {
    composable(route = homeVoiceRoute) {
        MainScaffoldWithDrawer(navController) { innerPadding ->
            HomeVoiceScreen(
                onNavigateToRecorder = {
                    navController.navigateToVoiceRecorder()
                },
                onNavigateToOpenFile = {
                    navController.navigateToAudioImport()
                },
                onNavigateToTextToSpeechScreen = {
                    navController.navigateTextToSpeech()
                },
                onNavigateToAudioList = {
                    navController.navigateToAudioList()
                },
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}