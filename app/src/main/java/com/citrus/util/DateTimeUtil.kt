package com.citrus.util


import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.stream.Collectors
import java.util.stream.IntStream

/**
 * refer : https://docs.oracle.com/javase/7/docs/api/java/text/SimpleDateFormat.html
 * refer: https://docs.oracle.com/javase/8/docs/api/java/time/format/DateTimeFormatter.html
 */
object DateTimeUtil {
    val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val dateTimeZoneFormFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss XXX") //2022-07-13 08:49:15 +05:30
    val dateYearFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd")
    val dateFormatter2: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")
    val dateFormatter3: DateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd HH:mm:ss")
    val dateFormatter4: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")
    val HHmmFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    val defaultZoneId = ZoneId.systemDefault().rules.getOffset(LocalDateTime.now()).id //like: +08:00

    fun getCurrentTimeByFormat(formatterString: String): String {
        return DateTimeFormatter.ofPattern(formatterString).format(LocalDateTime.now())
    }

    fun getCurrentTimeSlash(): String { //for old線上點單API
        return DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now())
    }

    fun getCurrentTime(): String {
        return dateTimeFormatter.format(LocalDateTime.now())
    }

    fun getCurrentDateTimeZoned(): String {
        return LocalDateTime.now().atZone(ZoneId.systemDefault()).format(dateTimeZoneFormFormatter)
    }

    fun getDateZoned(): String {
        return LocalDate.now().atStartOfDay(ZoneId.systemDefault()).format(dateTimeZoneFormFormatter)
    }

    fun getTodayDate(): String {
        return dateYearFormatter.format(LocalDate.now())
    }

    fun getDateOnly(): String {
        return dateFormatter.format(LocalDateTime.now())
    }

    fun getHour(): String {
        return DateTimeFormatter.ofPattern("HH").format(LocalDateTime.now())
    }


    fun getDatesBetweenTwoDates(startDate: LocalDate, endDate: LocalDate): List<LocalDate> {
        val numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate)
        return IntStream.iterate(0) { i -> i + 1 }
            .limit(numOfDaysBetween)
            .mapToObj { i -> startDate.plusDays(i.toLong()) }
            .collect(Collectors.toList())
    }
}