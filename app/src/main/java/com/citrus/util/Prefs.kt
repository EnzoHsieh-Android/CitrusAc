package com.citrus.util

import android.app.Application
import com.citrus.di.SpEntryPoint
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject


class Prefs @Inject constructor(application: Application) {

    private val hiltEntryPoint = EntryPointAccessors.fromApplication(
        application,
        SpEntryPoint::class.java
    )
    private val prefs = hiltEntryPoint.sharedPreference()

    var languagePos: Int
        get() = prefs.getInt("lan", -1)
        set(value) = prefs.edit().putInt("lan", value).apply()

    var version: Int
        get() = prefs.getInt("ver", -1)
        set(value) = prefs.edit().putInt("ver", value).apply()

    var isLeave: Boolean
        get() = prefs.getBoolean("isLeave", false)
        set(value) = prefs.edit().putBoolean("isLeave", value).apply()

    var serverIp : String
        get() = prefs.getString("serverIp", "") ?: ""
        set(value) = prefs.edit().putString("serverIp", value).apply()

    var deviceId : String
        get() = prefs.getString("deviceId", "") ?: ""
        set(value) = prefs.edit().putString("deviceId", value).apply()

    var storeName : String
        get() = prefs.getString("storeName", "") ?: ""
        set(value) = prefs.edit().putString("storeName", value).apply()

}
