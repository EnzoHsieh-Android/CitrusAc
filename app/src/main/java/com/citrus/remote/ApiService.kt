package com.citrus.remote


import com.citrus.remote.vo.*
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.*


interface ApiService {
    @GET
    suspend fun getAcSerial(
        @Url url: String
    ): ApiResponse<CustomSerialResult>

    @GET
    suspend fun getAcSerial2(
        @Url url: String
    ): ApiResponse<CustomSerialResult>

    @GET
    suspend fun getAcLatest(
        @Url url: String
    ): ApiResponse<AccessLatestResult>

    @GET
    suspend fun getAcHistory(
        @Url url: String,
        @Query("jsonData") jsonStr: String
    ): ApiResponse<AccessHistoryResult>

    @GET
    suspend fun getAcDetail(
        @Url url: String,
        @Query("jsonData") jsonStr: String
    ): ApiResponse<AccessDetailResult>

    @GET
    suspend fun setAcData(
        @Url url: String,
        @Query("jsonData") jsonStr: String
    ): ApiResponse<AccessDataResult>

}