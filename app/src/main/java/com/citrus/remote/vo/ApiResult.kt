package com.citrus.remote.vo

data class ApiResult<T>(
    val status: Int,
    val data: T? = null
)
