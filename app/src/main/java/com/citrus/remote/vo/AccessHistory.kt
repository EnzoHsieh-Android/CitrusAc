package com.citrus.remote.vo

import com.squareup.moshi.Json


data class AccessHistoryRequest(
    @Json(name = "LogTime_Start")
    val logTimeStart: String,
    @Json(name = "LogTime_End")
    val logTimeEnd: String,
    @Json(name = "QueryStr")
    val queryStr: String,
)

data class AccessHistory(
    @Json(name = "CustNo")
    val custNo: String,
    @Json(name = "Name")
    val name: String,
    @Json(name = "PID")
    val pid: String,
    @Json(name = "Birth")
    val birth: String,
    @Json(name = "PhotoPath")
    val photoPath: String,
    @Json(name = "LogTime")
    val logTime: String,
    @Json(name = "Status")
    val status: String,
    @Json(ignore = true)
    var selected: Boolean = false
)