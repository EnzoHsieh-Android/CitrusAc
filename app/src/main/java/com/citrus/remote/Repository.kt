package com.citrus.remote


import com.citrus.remote.vo.*
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun setOrdersPeriodByStatus(
        url: String,
        jsonData: String
    ): Flow<Resource<ResponseEnableQR>>

    suspend fun getAcSerial(url: String): Flow<Resource<CustomSerial>>
    suspend fun getAcHistory(url: String, jsonData: String): Flow<Resource<List<AccessHistory>>>
    suspend fun setAcData(url: String, jsonData: String): Flow<Resource<AccessDataResult>>
    suspend fun getAcLatest(url: String): Flow<Resource<List<AccessLatest>>>
    suspend fun getAcDetail(url: String, jsonData: String): Flow<Resource<AccessLatest>>
}