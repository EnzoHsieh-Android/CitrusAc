package com.citrus.util.ext

import com.citrus.util.DateTimeUtil.HHmmFormatter
import com.citrus.util.DateTimeUtil.dateFormatter
import com.citrus.util.DateTimeUtil.dateFormatter2
import com.citrus.util.DateTimeUtil.dateTimeFormatter
import com.citrus.util.DateTimeUtil.dateTimeZoneFormFormatter
import com.citrus.util.DateTimeUtil.dateYearFormatter
import com.citrus.util.DateTimeUtil.timeFormatter
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.*


/**
 * Date 是舊的日期格式，後來改成用 LocalDateTime
 * 但日曆套件還是用Date
 */

fun Date.toLocalDateTime(): LocalDateTime {
    return LocalDateTime.ofInstant(this.toInstant(), ZoneId.systemDefault())
}

fun LocalDateTime.toDate(): Date {
    return Date.from(this.atZone(ZoneId.systemDefault()).toInstant())
}

fun LocalDate.toDate(): Date {
    return Date.from(this.atStartOfDay(ZoneId.systemDefault()).toInstant())
}

//TODO 確認這些localDateTime有無正常 work

fun LocalDateTime.toHourMinStr(): String {
    return HHmmFormatter.format(this)
}

fun LocalDateTime.toDateYearStr(): String {
    return dateYearFormatter.format(this)
}

fun LocalDateTime.toDateStr(): String {
    return dateFormatter.format(this)
}


fun LocalDateTime.toDateStr2(): String {
    return dateFormatter2.format(this)
}


fun LocalDateTime.toDateTimeStr(): String {
    return dateTimeFormatter.format(this)
}

fun LocalDateTime.toStrWithAppZoned(): String {
    return dateTimeZoneFormFormatter.format(this.atZone(ZoneId.systemDefault()))
}

fun String.toDate(): Date {
    return try {
        val ldt = LocalDateTime.parse(this, dateTimeFormatter)
        Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant())
    } catch (e: Exception) {
        Date()
    }
}

/**
 * from "2022-07-13 08:49:15 +05:30" to [LocalDateTime] type
 */
fun String.toLocalDateTimeWithAppTimeZone(): LocalDateTime {
    return try {
        ZonedDateTime.parse(this, dateTimeZoneFormFormatter).withZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
    } catch (e: Exception) {
        LocalDateTime.now()
    }
}

fun String.toLocalTime(): LocalTime {
    return try {
        LocalTime.parse(this, timeFormatter)
    } catch (e: Exception) {
        LocalTime.of(0, 0, 0)
    }
}

/**
 * from "2022-07-13 08:49:15 +05:30" to [LocalDateTime] String
 * 'yyyy-MM-dd HH:mm:ss XXX' to 'yyyy-MM-dd HH:mm:ss'
 */
fun String.toAppTimeZoneStr(): String? {
    return try {
        ZonedDateTime.parse(this, dateTimeZoneFormFormatter).withZoneSameInstant(ZoneId.systemDefault()).format(dateTimeFormatter)
    } catch (e: Exception) {
        null
    }
}