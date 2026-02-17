package com.pixelzlab.app.feature.text_to_speech.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.pixelzlab.app.feature.text_to_speech.TextToSpeechScreen

const val textToSpeechRoute = "text_to_speech_route"

fun NavController.navigateTextToSpeech(navOptions: NavOptions?=null){
    this.navigate(textToSpeechRoute, navOptions)
}

fun NavGraphBuilder.textToSpeechScreen(navController: NavHostController){
    composable(textToSpeechRoute){
        TextToSpeechScreen(navController)
    }
}