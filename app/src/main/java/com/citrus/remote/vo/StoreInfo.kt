package com.citrus.remote.vo

import com.squareup.moshi.Json

//"OpenHR": 6,
//"MathCount": 0,
//"StoreCompNo": "C00001",
//"StoreName": "大魯閣滑輪場新時代館"

data class StoreInfo(
    @Json(name = "OpenHR")
    val openHR: Int,
    @Json(name = "MathCount")
    val mathCount: Int,
    @Json(name = "StoreCompNo")
    val storeCompNo: String,
    @Json(name = "StoreName")
    val storeName: String
)