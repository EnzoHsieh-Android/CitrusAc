package com.citrus.remote.vo

import com.squareup.moshi.Json


data class AccessData(
    @Json(name = "CustNo")
    val custNo: String,
    @Json(name = "DeviceID")
    val deviceId: String,
    @Json(name = "Status")
    val status: String,
    @Json(name = "Memo")
    val memo: String
)
