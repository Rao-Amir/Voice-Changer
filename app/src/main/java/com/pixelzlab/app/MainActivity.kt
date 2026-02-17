package com.pixelzlab.app

import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.compose.rememberNavController
import com.pixelzlab.app.designsystem.theme.AppTheme
import com.pixelzlab.app.navigation.AppNavHost
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val appReady = MutableStateFlow(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen before calling setContent
        val splashScreen = installSplashScreen()
        // Configure splash screen to stay on-screen until app is ready
        splashScreen.setKeepOnScreenCondition { !appReady.value }
        
        super.onCreate(savedInstanceState)
        
        // Simulate work for splash screen
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                delay(1000) // Simulate some initialization work
                appReady.update { true }
            }
        }
        
        enableEdgeToEdge()
        setContent {
            val darkTheme = isSystemInDarkTheme()
            
            // Update system bars based on theme
            DisposableEffect(darkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = if (darkTheme) {
                        SystemBarStyle.dark(Color.Transparent.toArgb())
                    } else {
                        SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb())
                    },
                    navigationBarStyle = if (darkTheme) {
                        SystemBarStyle.dark(Color.Transparent.toArgb())
                    } else {
                        SystemBarStyle.light(Color.Transparent.toArgb(), Color.Transparent.toArgb())
                    }
                )
                onDispose {}
            }
            
            AppContent()
        }
    }
}

@Composable
fun AppContent() {
    AppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background,
        ) {
            val navController = rememberNavController()
//            Scaffold { padding ->
                AppNavHost(
                    navController = navController,
                    modifier = Modifier
                        .fillMaxSize()
//                        .padding(padding)
                )
//            }
        }
    }
}
