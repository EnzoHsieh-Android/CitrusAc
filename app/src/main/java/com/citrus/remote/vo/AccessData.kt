package com.citrus.remote.vo

import com.squareup.moshi.Json


data class RequestSpecMemberMemo(
    @Json(name = "CustNo")
    val custNo: String
)

data class RequestSpecMemberRes(
    @Json(name = "CustNo")
    val custNo: String,
    @Json(name = "ReservationTime")
    val reservationTime : String? = null
)


data class RequestMemoValid(
    @Json(name = "CustNo")
    val custNo: String,
    @Json(name = "Seq")
    val seq: List<Int>
)

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
