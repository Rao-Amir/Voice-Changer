package com.pixelzlab.app.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Adaptive layout that adjusts based on available screen width
 * Similar to pattern used in Now in Android
 */
@Composable
fun AdaptiveContentLayout(
    compact: @Composable () -> Unit,
    medium: @Composable () -> Unit = compact,
    expanded: @Composable () -> Unit = medium,
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val width = maxWidth
        
        when {
            // Phone (< 600dp)
            width < 600.dp -> compact()
            
            // Small tablet (600dp - 840dp)
            width < 840.dp -> medium()
            
            // Large tablet / desktop (> 840dp)
            else -> expanded()
        }
    }
}

/**
 * Layout for showing a list and detail side by side on larger screens
 * (similar to Now in Android pattern)
 */
@Composable
fun ListDetailLayout(
    list: @Composable () -> Unit,
    detail: @Composable () -> Unit,
    showListOnly: Boolean = false,
    listWeight: Float = 0.33f,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val width = maxWidth
        
        if (width < 840.dp || showListOnly) {
            // Show only list or detail in compact mode
            if (showListOnly) {
                Box(modifier = Modifier.fillMaxSize()) { list() }
            } else {
                Box(modifier = Modifier.fillMaxSize()) { detail() }
            }
        } else {
            // Show side-by-side in expanded mode
            Row(modifier = Modifier.fillMaxSize()) {
                Surface(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(listWeight)
                        .wrapContentWidth(unbounded = false),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    list()
                }
                
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    detail()
                }
            }
        }
    }
} 