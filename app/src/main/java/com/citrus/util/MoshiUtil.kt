package com.citrus.util

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

object MoshiUtil {

    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    fun <T> toJson(adapter: JsonAdapter<T>, src: T, indent: String = ""): String {
        try {
            return adapter.indent(indent).toJson(src)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    inline fun <reified T> toJson(src: T, indent: String = ""): String {
        val adapter = moshi.adapter(T::class.java)
        return this.toJson(adapter = adapter, src = src, indent = indent)
    }

}