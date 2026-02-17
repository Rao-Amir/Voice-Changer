package com.pixelzlab.app.utils.extension

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity

//region Multiple permissions
inline fun Fragment.askForMultiplePermissions(
    crossinline onDenied: () -> Unit = {},
    crossinline onPermissionsGranted: () -> Unit = {}
): ActivityResultLauncher<Array<String>> =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        val granted = result.map { it.value }.filter { it == false }
        if (granted.isNullOrEmpty()) {
            onPermissionsGranted()
        } else {
            onDenied()
        }
    }

inline fun FragmentActivity.askForMultiplePermissions(
    crossinline onDenied: () -> Unit = {},
    crossinline onPermissionsGranted: () -> Unit = {}
): ActivityResultLauncher<Array<String>> =
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        val granted = result.map { it.value }.filter { it == false }
        if (granted.isNullOrEmpty()) {
            onPermissionsGranted()
        } else {
            onDenied()
        }
    }
//endregion

//region Single permission
inline fun Fragment.askForSinglePermission(
    crossinline onDenied: () -> Unit = {},
    crossinline onPermissionsGranted: () -> Unit = {}
): ActivityResultLauncher<String> =
    registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            onPermissionsGranted()
        } else {
            onDenied()
        }
    }

inline fun FragmentActivity.askForSinglePermission(
    crossinline onDenied: () -> Unit = {},
    crossinline onPermissionsGranted: () -> Unit = {}
): ActivityResultLauncher<String> =
    registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        if (it) {
            onPermissionsGranted()
        } else {
            onDenied()
        }
    }
//endregion
