package com.pixelzlab.app.core.network

import android.content.Context
import com.pixelzlab.app.R

fun Throwable.message(context: Context): String {
    return when (this) {
        is ApiException -> error?.message
        else -> message
    } ?: context.getString(R.string.error_generic)
}
