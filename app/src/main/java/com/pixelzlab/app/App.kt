package com.pixelzlab.app

import android.app.Application
import android.os.StrictMode
import androidx.tracing.Trace
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import javax.inject.Provider

/**
 * Application class with enhanced startup monitoring and performance features
 */
@HiltAndroidApp
class App : Application(), ImageLoaderFactory {

    @Inject
    lateinit var imageLoader: Provider<ImageLoader>

    override fun onCreate() {
        // Enable tracing for app startup
        Trace.beginSection("AppStartup")
        super.onCreate()
        
        setupStrictMode()

        MobileAds.initialize(this) {}
        
        Trace.endSection()
    }

    /**
     * Setup StrictMode for development builds
     */
    private fun setupStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectDiskReads()
                    .detectDiskWrites()
                    .detectNetwork()
                    .penaltyLog()
                    .build()
            )
            
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .detectActivityLeaks()
                    .penaltyLog()
                    .build()
            )
        }
    }

    /**
     * Provide Coil ImageLoader with SVG support
     * Now using Hilt to inject the ImageLoader for better testability
     */
    override fun newImageLoader(): ImageLoader {
        return imageLoader.get()
    }
}