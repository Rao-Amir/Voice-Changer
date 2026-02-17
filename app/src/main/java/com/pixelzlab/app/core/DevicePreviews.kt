package com.pixelzlab.app.core

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/**
 * Preview annotation for phone form factor in both dark and light modes
 */
@Preview(
    name = "Phone - Light",
    uiMode = Configuration.UI_MODE_NIGHT_NO,
    showBackground = true,
)
@Preview(
    name = "Phone - Dark",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
)
annotation class PhoneDevicePreviews

@Preview(name = "Tablet", device = "spec:width=1280dp,height=800dp,dpi=480")
annotation class TabletDevicePreviews

@Preview(name = "Landscape", device = "spec:width=640dp,height=360dp,dpi=480")
annotation class LandscapeDevicePreviews

@Preview(name = "Foldable", device = "spec:width=673dp,height=841dp,dpi=480")
annotation class FoldableDevicePreviews

@PhoneDevicePreviews
@TabletDevicePreviews
@LandscapeDevicePreviews
@FoldableDevicePreviews
annotation class DevicePreviews