package com.citrus.remote.vo

import com.squareup.moshi.Json

data class AcDetailRequest(
    @Json(name = "CustNo")
    val custNo: String
)

