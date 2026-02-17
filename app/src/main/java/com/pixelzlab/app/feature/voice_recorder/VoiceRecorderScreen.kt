package com.pixelzlab.app.feature.voice_recorder

import android.app.Activity
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.NavHostController
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.pixelzlab.app.R
import com.pixelzlab.app.feature.audio_effect.navigation.navigateToApplyEffect
import com.pixelzlab.app.feature.voice_recorder.fragment.VoiceRecorderFragment

@Composable
fun VoiceRecorderScreen(
    navController: NavHostController
) {
    val context = LocalContext.current
    val view = LocalView.current
    val fragmentManager = (view.context as FragmentActivity).supportFragmentManager
    val fragmentId = remember { View.generateViewId() }
    val isRecording = remember { mutableStateOf(false) }
    val showExitDialog = remember { mutableStateOf(false) }

    var interstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }

    fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(
            context,
            "ca-app-pub-3940256099942544/1033173712",
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        loadInterstitialAd()
    }

    BackHandler(enabled = true) {
        if (isRecording.value) {
            showExitDialog.value = true
        } else {
            navController.popBackStack()
        }
    }

    if (showExitDialog.value) {
        AlertDialog(
            onDismissRequest = { showExitDialog.value = false },
            title = { Text(stringResource(R.string.exit_confirmation_title)) },
            text = { Text(stringResource(R.string.exit_confirmation_message)) },
            confirmButton = {
                androidx.compose.material3.TextButton(onClick = {
                    if (interstitialAd != null) {
                        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                showExitDialog.value = false
                                navController.popBackStack()
                                loadInterstitialAd()
                            }

                            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                                showExitDialog.value = false
                                navController.popBackStack()
                                loadInterstitialAd()
                            }
                        }
                        interstitialAd?.show(context as Activity)
                    } else {
                        showExitDialog.value = false
                        navController.popBackStack()
                    }
                }) { Text(stringResource(R.string.exit)) }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = {
                    if (interstitialAd != null) {
                        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                showExitDialog.value = false
                                loadInterstitialAd()
                            }

                            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                                showExitDialog.value = false
                                loadInterstitialAd()
                            }
                        }
                        interstitialAd?.show(context as Activity)
                    } else {
                        showExitDialog.value = false
                    }
                }) { Text(stringResource(R.string.stay)) }
            }
        )
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.recorder_title)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (isRecording.value) showExitDialog.value = true
                            else navController.popBackStack()
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    FragmentContainerView(ctx).apply {
                        id = fragmentId
                        if (fragmentManager.findFragmentById(id) == null) {
                            val fragment = VoiceRecorderFragment()
                            fragment.onNavigationApplyEffect = { filePath ->
                                if (interstitialAd != null) {
                                    interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                                        override fun onAdDismissedFullScreenContent() {
                                            navController.navigateToApplyEffect(filePath)
                                            loadInterstitialAd()
                                        }

                                        override fun onAdFailedToShowFullScreenContent(error: AdError) {
                                            navController.navigateToApplyEffect(filePath)
                                            loadInterstitialAd()
                                        }
                                    }
                                    interstitialAd?.show(view.context as Activity)
                                } else {
                                    navController.navigateToApplyEffect(filePath)
                                }
                            }
                            fragment.onRecordingStateChanged = { recording ->
                                isRecording.value = recording
                            }
                            fragmentManager.beginTransaction().add(id, fragment).commit()
                        }

                    }
                }
            )
        }

    }

}
