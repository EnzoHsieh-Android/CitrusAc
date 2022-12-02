package com.citrus.util

import android.annotation.SuppressLint
import android.content.Context
import com.citrus.citrusac.R
import com.citrus.di.prefs
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import splitties.views.textColorResource
import java.text.SimpleDateFormat
import java.util.*


object Constants {
    var screenW = 0
    var screenH = 0
    const val SUCCESS = "success"
    const val ERROR = "error"
    const val NO_DATA = "No Data"

    const val BASE_URL = "https://cms.citrus.tw"

    const val SHARED_PREFERENCES_NAME = "sharedPref"

    const val GET_STORE_DATA = "/CitrusAC/Service.asmx/GetStoreData"
    const val GET_SERIAL = "/CitrusAC/Service.asmx/GetCustSerial"
    const val GET_MEMBER_DETAIL = "/CitrusAC/Service.asmx/GetMemberDetail"
    const val GET_RECORD_HISTORY = "/CitrusAC/Service.asmx/GetRecordHistoryList"
    const val GET_RECORD_LATEST = "/CitrusAC/Service.asmx/GetRecordLatestList"
    const val SET_ACCESS_DATA = "/AC/Service.asmx/SetCustAccessData"
    const val DOWNLOAD_URL = "http://hq.citrus.tw/apk/"
    val serverDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

    fun getServerIP(): String {
        return prefs.serverIp
    }

    val dateTimeFormatSql = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")

    val dateTimeFormatSqlRes = SimpleDateFormat("yyyy/MM/dd HH:mm")

    val dateFormatSql = SimpleDateFormat("yyyy/MM/dd")

    val timeFormatSql = SimpleDateFormat("HH:mm:ss")

    @SuppressLint("SimpleDateFormat")
    fun getCurrentTime(): String {
        val currentDate = Calendar.getInstance().time
        val sdf = dateTimeFormatSql
        return sdf.format(currentDate)
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentDate(): String {
        val currentDate = Calendar.getInstance().time
        val sdf = dateFormatSql
        return sdf.format(currentDate)
    }

    fun getFromServerTime(serverTime: String?): String {
        serverTime?.let {
            val date = serverDateFormat.parse(serverTime)
            return dateFormatSql.format(date)
        } ?: return getCurrentTime()
    }

    fun getResServerTime(serverTime: String?): String {
        serverTime?.let {
            val date = serverDateFormat.parse(serverTime)
            return dateTimeFormatSqlRes.format(date)
        } ?: return getCurrentTime()
    }

    fun getLogServerTime(serverTime: String?): String {
        serverTime?.let {
            val date = serverDateFormat.parse(serverTime)
            return timeFormatSql.format(date)
        } ?: return getCurrentTime()
    }

    fun getLogServerDateTime(serverTime: String?): String {
        serverTime?.let {
            val date = serverDateFormat.parse(serverTime)
            return dateTimeFormatSql.format(date)
        } ?: return getCurrentTime()
    }


    fun setChips(
        context: Context,
        list: List<String>,
        chipGroup: ChipGroup,
        fontSize: Float,
        isEnable: Boolean = true
    ) {
        chipGroup.removeAllViews()
        val scale: Float = context.resources.configuration.fontScale
        for (s in list) {
            val chip = Chip(context)
            chip.text = s
            chip.textSize = context.px2sp(fontSize)
            chip.textColorResource = R.color.colorPrimary
            chip.chipEndPadding = context.resources.getDimension(R.dimen.dp_10)
            chip.chipStartPadding = context.resources.getDimension(R.dimen.dp_10)
            chip.isClickable = false
            chip.chipMinHeight = 40 * scale

            chipGroup.addView(chip)
        }
    }

    fun Context.px2sp(pxValue: Float): Float {
        val fontScale = this.resources.displayMetrics.scaledDensity
        return pxValue / fontScale + 0.5f
    }

}