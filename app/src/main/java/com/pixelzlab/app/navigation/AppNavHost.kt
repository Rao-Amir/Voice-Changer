package com.pixelzlab.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.pixelzlab.app.feature.audio_effect.navigation.audioEffectScreen
import com.pixelzlab.app.feature.audio_import.navigation.audioImportScreen
import com.pixelzlab.app.feature.audio_list.navigation.audioListScreen
import com.pixelzlab.app.feature.audio_saved.audioSavedScreen
import com.pixelzlab.app.feature.detail.navigation.detailScreen
import com.pixelzlab.app.feature.detail.navigation.navigateToDetail
import com.pixelzlab.app.feature.home.navigation.homeScreen
import com.pixelzlab.app.feature.home_voice.navigation.homeVoiceScreen
import com.pixelzlab.app.feature.home_voice.navigation.navigateToHomeVoice
import com.pixelzlab.app.feature.splash.navigation.splashRoute
import com.pixelzlab.app.feature.splash.navigation.splashScreen
import com.pixelzlab.app.feature.text_to_speech.navigation.textToSpeechScreen
import com.pixelzlab.app.feature.voice_recorder.navigation.voiceRecorderScreen

/**
 * App navigation graph using type-safe navigation with nested routes
 */
@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = splashRoute
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
    ) {
        splashScreen(
            onSplashFinished = { navController.navigateToHomeVoice() }
        )
//
//        homeScreen(
//            onPokemonClick = { pokemon -> navController.navigateToDetail(pokemon) }
//        )
//
//        detailScreen(
//            onBackClick = { navController.navigateUp() }
//        )
        homeVoiceScreen(navController)
        voiceRecorderScreen(navController)
        audioImportScreen(navController)
        textToSpeechScreen(navController)
        audioListScreen(navController)
        audioEffectScreen(navController)
        audioSavedScreen(navController)
    }
} 