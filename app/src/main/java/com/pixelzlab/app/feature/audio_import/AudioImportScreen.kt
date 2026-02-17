package com.pixelzlab.app.feature.audio_import

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.pixelzlab.app.R
import com.pixelzlab.app.feature.audio_import.fragment.AudioImportFragment


@Composable
fun AudioImportScreen(
    navHostController: NavHostController,
    param: (Any) -> Unit
) {
    val view = LocalView.current
    val context = LocalLifecycleOwner.current
    val fragmentManager = (view.context as FragmentActivity).supportFragmentManager
    val fragmentId = remember { View.generateViewId() }

    DisposableEffect(Unit) {
        val listener: (String, Bundle) -> Unit = { _, bundle ->
            val uri = bundle.getParcelable<Uri>("uri")
            if (uri != null) {
                param(uri)
            }
        }
        fragmentManager.setFragmentResultListener("audio_pick_result", context, listener)
        onDispose {
            fragmentManager.clearFragmentResultListener("audio_pick_result")
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.select_audio_file)) },
                navigationIcon = {
                    IconButton(onClick = {navHostController.popBackStack()}) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back)
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
                        if (fragmentManager.findFragmentById(fragmentId) == null) {
                            fragmentManager.beginTransaction()
                                .replace(fragmentId, AudioImportFragment())
                                .commit()
                        }
                    }
                }
            )
        }
    }
}