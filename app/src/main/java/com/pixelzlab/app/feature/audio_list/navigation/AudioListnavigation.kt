package com.pixelzlab.app.feature.audio_list.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.pixelzlab.app.feature.audio_list.AudioListScreen

const val audio_list_route = "audio_list_route"

fun NavController.navigateToAudioList(navOptions: NavOptions?=null){
    this.navigate(audio_list_route, navOptions)
}

fun NavGraphBuilder.audioListScreen(navController: NavHostController){
    composable(audio_list_route){
        AudioListScreen(navController)
    }
}