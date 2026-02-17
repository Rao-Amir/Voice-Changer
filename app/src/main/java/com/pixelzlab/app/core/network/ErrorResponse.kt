package com.pixelzlab.app.core.network

import com.squareup.moshi.Json

/**
 * Created by pixelzlab on 23/04/2023.
 */
data class ErrorResponse(
    @Json(name = "message")
    val message: String
)