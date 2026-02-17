package com.pixelzlab.app.startup

import android.content.Context
import androidx.startup.Initializer
import com.pixelzlab.app.BuildConfig
import timber.log.Timber

/**
 * Initializer for app startup tasks using the App Startup library
 */
class AppInitializer : Initializer<Unit> {
    override fun create(context: Context) {
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        
        // Other startup initialization can go here
        Timber.d("App initialized")
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
} 