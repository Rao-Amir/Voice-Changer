package com.pixelzlab.app.feature.audio_effect

import android.app.Activity
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.runtime.rememberCoroutineScope
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
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.pixelzlab.app.R
import com.pixelzlab.app.feature.audio_effect.fragment.AudioEffectFragment
import com.pixelzlab.app.feature.audio_saved.navigateAudioSaved
import kotlinx.coroutines.launch

@Composable
fun AudioEffectScreen(
    navHostController: NavHostController,
    audioFilePath: String
) {
    val view = LocalView.current
    val context = LocalContext.current
    val fragmentManager = (view.context as FragmentActivity).supportFragmentManager
    val fragmentId = remember { View.generateViewId() }
    val activity = LocalView.current.context as FragmentActivity

    var showSaveDialog by remember { mutableStateOf(false) }
    var showExitDialog by remember { mutableStateOf(false) }

    var defaultFileName by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    var rewardedAd by remember { mutableStateOf<RewardedAd?>(null) }
    var interstitialAd by remember { mutableStateOf<InterstitialAd?>(null) }

    fun loadRewardedAd() {
        val adRequest = AdRequest.Builder().build()
        RewardedAd.load(
            context,
            "ca-app-pub-3940256099942544/5224354917",
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    rewardedAd = null
                }

                override fun onAdLoaded(ad: RewardedAd) {
                    rewardedAd = ad
                }
            }
        )
    }

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
        loadRewardedAd()
        loadInterstitialAd()
    }

    if (showSaveDialog) {
        SaveFileNameDialog(
            initialName = defaultFileName,
            onDismiss = { showSaveDialog = false },
            onSave = { fileName ->
                showSaveDialog = false

                val progressView = activity.findViewById<View>(R.id.progress_loading)
                progressView?.visibility = View.VISIBLE

                val fragment = fragmentManager.findFragmentById(fragmentId)
                if (fragment is AudioEffectFragment) {
                    coroutineScope.launch {
                        try {
                            val savedFile = fragment.saveEffectedAudio(fileName)
                            fragmentManager.beginTransaction().remove(fragment).commit()
                            navHostController.navigateAudioSaved(savedFile.absolutePath)
                        } finally {
                            progressView?.visibility = View.GONE
                        }

                    }
                }
                showSaveDialog = false
            }
        )
    }

    BackHandler {
        showExitDialog = true
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text(stringResource(R.string.exit_apply_effect_title)) },
            text = { Text(stringResource(R.string.exit_apply_effect_message)) },
            confirmButton = {
                Button(onClick = {
                    if (interstitialAd != null) {
                        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                showExitDialog = false
                                val fragment = fragmentManager.findFragmentById(fragmentId)
                                if (fragment is AudioEffectFragment) {
                                    fragment.stopAudio()
                                    fragmentManager.beginTransaction().remove(fragment).commit()
                                }
                                navHostController.popBackStack()
                                loadInterstitialAd()
                            }

                            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                                showExitDialog = false
                                val fragment = fragmentManager.findFragmentById(fragmentId)
                                if (fragment is AudioEffectFragment) {
                                    fragment.stopAudio()
                                    fragmentManager.beginTransaction().remove(fragment).commit()
                                }
                                navHostController.popBackStack()
                                loadInterstitialAd()
                            }
                        }
                        interstitialAd?.show(activity)
                    } else {
                        showExitDialog = false
                        val fragment = fragmentManager.findFragmentById(fragmentId)
                        if (fragment is AudioEffectFragment) {
                            fragment.stopAudio()
                            fragmentManager.beginTransaction().remove(fragment).commit()
                        }
                        navHostController.popBackStack()
                    }
                }) {
                    Text(stringResource(R.string.exit))
                }
            },
            dismissButton = {
                Button(onClick = {
                    if (interstitialAd != null) {
                        interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                            override fun onAdDismissedFullScreenContent() {
                                showExitDialog = false
                                loadInterstitialAd()
                            }

                            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                                showExitDialog = false
                                loadInterstitialAd()
                            }
                        }
                        interstitialAd?.show(activity)
                    } else {
                        showExitDialog = false
                    }
                }) {
                    Text(stringResource(R.string.stay))
                }
            }
        )
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.select_effect)) },
                navigationIcon = {
                    IconButton(
                        onClick = {
                        val fragment = fragmentManager.findFragmentById(fragmentId)
                        if (fragment is AudioEffectFragment) {
                            fragment.stopAudio() // Dừng phát nhạc
                        }
//
//                        fragmentManager.beginTransaction().remove(fragment!!).commit()
//                        navHostController.popBackStack()

                            showExitDialog = true
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val fragment = fragmentManager.findFragmentById(fragmentId)
                            if (fragment is AudioEffectFragment) {
                                fragment.stopAudio()

                                defaultFileName =
                                    "${fragment.currentEffect}_${System.currentTimeMillis()}.wav"

                                if (rewardedAd != null) {
                                    rewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                                        override fun onAdDismissedFullScreenContent() {
                                            showSaveDialog = true
                                            loadRewardedAd()
                                        }

                                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                                            showSaveDialog = true
                                            loadRewardedAd()
                                        }
                                    }
                                    rewardedAd?.show(activity) {
                                        // User earned reward
                                    }
                                } else {
                                    showSaveDialog = true
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = stringResource(R.string.save)
                        )
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
                            fragmentManager.beginTransaction()
                                .add(id, AudioEffectFragment.newInstance(audioFilePath))
                                .commit()
                        }
                    }
                }
            )
        }
    }

}
