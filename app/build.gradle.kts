plugins {
    id("android.application")
    id("android.compose")
    id("android.hilt")
    id("kotlin-parcelize")
    id("dagger.hilt.android.plugin")
    id("com.google.devtools.ksp")
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.0"
//    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.pixelzlab.app"

    defaultConfig {
        applicationId = "com.pixelzlab.app"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        debug {
            buildConfigField("String", "BASE_API_URL", "\"https://pokeapi.co/api/v2/\"")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                file("proguard-rules.pro")
            )
            buildConfigField("String", "BASE_API_URL", "\"https://pokeapi.co/api/v2/\"")
        }
      create("benchmark") {
        signingConfig = signingConfigs.getByName("debug")
        matchingFallbacks += listOf("release")
        isDebuggable = false
      }
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    applicationVariants.all {
        outputs.all {
            val outputImpl = this as com.android.build.gradle.internal.api.BaseVariantOutputImpl
            outputImpl.outputFileName = "VoiceChangerNative.apk"
        }
    }
}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.compose.ui.util)
    implementation(libs.compose.ui.tooling)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.foundation.layout)
    implementation(libs.compose.material)
    implementation(libs.compose.material3)
    // Material Icon Extended
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    implementation(libs.coil.kt)
    implementation(libs.coil.kt.compose)
    implementation(libs.coil.kt.svg)

    implementation(libs.kotlinx.coroutines.android)

    //Exo Player
//    implementation("androidx.media3:media3-exoplayer:1.4.0")
//    implementation("androidx.media3:media3-exoplayer-dash:1.4.0")
//    implementation("com.mrljdx:ffmpegkit-kmp-android:0.1.3")
//    implementation("com.github.st-h:TarsosDSP:2.4.1")
//    implementation ("com.arthenica:ffmpeg-kit-full-gpl:6.0-2")
//    implementation("")
//    implementation("com.github.iDeMonnnnnn.DeMon_Sound:FmodSound:1.1")
//    implementation("com.github.iDeMonnnnnn.DeMon_Sound:SoundCoding:1.1")
//
//    implementation("be.tarsos.dsp:core:2.5")
//    implementation("be.tarsos.dsp:jvm:2.5")

    // Kotlinx Serialization
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    implementation(libs.hilt.android)
    implementation(libs.hilt.navigation.compose)
    implementation(libs.material)
    implementation("androidx.compose.material3:material3:1.3.2")
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    ksp(libs.hilt.compiler)

    implementation(libs.dataStore.preferences)

    implementation(libs.moshi)
    implementation(libs.moshi.kotlin)

    implementation(libs.retrofit.core)
    implementation(libs.retrofit.converter.moshi)

    implementation(libs.okhttp.logging)

    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.paging.runtime)

    debugImplementation(libs.chucker.library)
    releaseImplementation(libs.chucker.library.no.op)

    implementation(libs.timber)

    //navigation XML
    implementation("androidx.navigation:navigation-fragment-ktx:2.9.0")
    implementation("androidx.navigation:navigation-ui-ktx:2.9.0")

    implementation(libs.navigation.compose)

    implementation(libs.lifecycle.runtime.compose)
    implementation(libs.lifecycle.viewModel.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.startup)
    implementation(libs.androidx.tracing.ktx)

    implementation(libs.play.services.ads)
}

if (file("google-services.json").exists()) {
    apply(plugin = libs.plugins.gms.googleServices.get().pluginId)
    apply(plugin = libs.plugins.firebase.crashlytics.get().pluginId)
}