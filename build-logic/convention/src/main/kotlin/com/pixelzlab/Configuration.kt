package com.pixelzlab

import org.gradle.api.JavaVersion

object Configuration {
    const val compileSdk = 35
    const val targetSdk = 35
    const val minSdk = 30
    const val buildToolsVersion = "30.0.3"
    const val majorVersion = 1
    const val minorVersion = 0
    const val patchVersion = 0
    const val versionName = "$majorVersion.$minorVersion.$patchVersion"
    const val versionCode = 1
    const val secretFile = "secrets.defaults.properties"
    val javaVersion = JavaVersion.VERSION_17
    val freeCompilerArgs = listOf(
        "-opt-in=kotlin.RequiresOptIn",
        // Enable experimental coroutines APIs, including Flow
        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        "-opt-in=kotlinx.coroutines.FlowPreview",
        "-opt-in=kotlin.Experimental",
        // Enable experimental compose APIs
        "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
        "-opt-in=androidx.lifecycle.compose.ExperimentalLifecycleComposeApi",
        "-opt-in=UnusedMaterial3ScaffoldPaddingParameter",
        "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
    )
}