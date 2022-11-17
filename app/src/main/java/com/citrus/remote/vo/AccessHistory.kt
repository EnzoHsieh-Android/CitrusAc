package com.citrus.remote.vo

import com.squareup.moshi.Json


//{"LogTime_Start":"2022/11/09","LogTime_End":"2022/11/11","CustNo":"","Name":"","PID":""}

data class AccessHistoryRequest(
    @Json(name = "LogTime_Start")
    val logTimeStart: String,
    @Json(name = "LogTime_End")
    val logTimeEnd: String,
    @Json(name = "QueryStr")
    val queryStr: String,
)

data class AccessHistoryResult(val status: Int, val data: List<AccessHistory>)

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
    @Json(name = "IsNote")
    val isNote: String,
    @Json(name = "Status")
    val status: String,
    @Transient
    var selected: Boolean = false
    )