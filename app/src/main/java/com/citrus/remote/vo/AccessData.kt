package com.citrus.remote.vo

import com.squareup.moshi.Json


//{"CustNo":"1700007", "DeviceID":"E01", "Status":"I", "Memo":""}
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

data class AccessDataResult(val status: Int, val data: String)