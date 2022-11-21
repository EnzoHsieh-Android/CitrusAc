package com.citrus.remote.vo

import com.squareup.moshi.Json


data class AccessLatestResult(val status: Int, val data: List<AccessLatest>?)

data class AccessLatest(
    @Json(name = "CustNo")
    val custNo: String,
    @Json(name = "Name")
    val name: String,
    @Json(name = "PID")
    val pid: String,
    @Json(name = "Birth")
    val birth: String,
    @Json(name = "LogTime")
    val logTime: String,
    @Json(name = "PhotoPath")
    var photoPath: String,
    @Json(name = "Notes")
    val notes: List<NoteData>,
    @Json(name = "Reservation")
    val resData: List<Reservation>,
    @Json(ignore = true)
    var selected: Boolean = false,
)


data class NoteData(
    @Json(name = "Seq")
    val seq: Int,
    @Json(name = "Note")
    var note: String,
)


data class Reservation(
    @Json(name = "ReservationTime")
    val resTime: String,
    @Json(name = "cust_num")
    val custNum: Int,
    @Json(name = "Memo")
    val memo: String,
    @Json(name = "Status")
    val status: String,
    @Json(name = "EmployeeName")
    val empName: String?,
    @Json(name = "Phone")
    val phone: String
)