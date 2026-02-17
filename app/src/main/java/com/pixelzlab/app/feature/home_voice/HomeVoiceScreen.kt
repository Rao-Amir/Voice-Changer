package com.pixelzlab.app.feature.home_voice

import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView

@Composable
fun HomeVoiceScreen (
    onNavigateToRecorder: () -> Unit,
    onNavigateToOpenFile: () -> Unit,
    onNavigateToTextToSpeechScreen: () -> Unit,
    onNavigateToAudioList: () ->Unit,
    modifier: Modifier = Modifier
){

    val view = LocalView.current
    val fragmentManager = (view.context as FragmentActivity).supportFragmentManager
    val fragmentId = remember { View.generateViewId() }

//    Scaffold (
//        modifier = Modifier.fillMaxSize()
//    ){ innerPadding ->
//
//
//    }
    Box(
        modifier = modifier
        ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = {ctx ->
                FragmentContainerView(ctx).apply {
                    id = fragmentId
                    if (fragmentManager.findFragmentById(id) == null) {
                        fragmentManager.beginTransaction().add(id, HomeVoiceFragment().apply {
                            this.onNavigateToRecorder = onNavigateToRecorder
                            this.onNavigateOpenFile = onNavigateToOpenFile
                            this.onNavigateToTextToSpeechScreen = onNavigateToTextToSpeechScreen
                            this.onNavigateToAudioList = onNavigateToAudioList
                        }).commit()
                    }
                }
            }
        )
    }
}