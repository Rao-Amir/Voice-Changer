package com.pixelzlab.app.model.base

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


/**
 * Created by pixelzlab on 16/09/2022.
 */
@JsonClass(generateAdapter = true)
class BaseResponse<T>(
    @Json(name = "count") val count: Int? = null,
    @Json(name = "results") val data: T?
)
