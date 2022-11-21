package com.citrus.remote.vo

import com.squareup.moshi.Json

data class AcDetailRequest(
    @Json(name = "CustNo")
    val custNo: String
)


data class AccessDetailResult(val status: Int, val data: AccessLatest?)
