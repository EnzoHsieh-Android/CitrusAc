package com.citrus.remote.vo

import com.citrus.di.prefs
import com.citrus.util.Constants.getCurrentTime
import com.squareup.moshi.Json


//{"rsNo":"C00000","QRCode":"C0000000001","status":"Y","perTime":"2022/11/02 11:18:19"}
data class RequestEnableQR(
    @Json(name = "RSNO")
    val rsNo: String,
    @Json(name = "QRCode")
    val qRCode: String,
    @Json(name = "Status")
    val status: String = if (prefs.isLeave) "B" else "Y",
    @Json(name = "PerTime")
    val perTime: String = getCurrentTime()
)

data class ResponseEnableQR(
    @Json(name = "data")
    val data: String,
    @Json(name = "status")
    val status: Int
)