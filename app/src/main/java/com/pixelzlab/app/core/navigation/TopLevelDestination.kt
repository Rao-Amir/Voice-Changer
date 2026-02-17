package com.pixelzlab.app.core.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

/**
 * Type for the top level destinations in the application. Each of these destinations
 * can contain one or more screens (based on the window size). Navigation from one screen to the
 * next within a single destination will be handled directly in composables.
 */
enum class TopLevelDestination(
    @DrawableRes val selectedIcon: Int = 0,
    @DrawableRes val unselectedIcon: Int = 0,
    @StringRes val titleTextId: Int = 0
) {
}
