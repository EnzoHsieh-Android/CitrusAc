package com.citrus.remote


import com.citrus.remote.vo.*
import com.skydoves.sandwich.ApiResponse
import retrofit2.http.*


interface ApiService {

    @GET
    suspend fun getStoreData(
        @Url url: String
    ): ApiResponse<ApiResult<StoreInfo>>

    @GET
    suspend fun getAcSerial(
        @Url url: String
    ): ApiResponse<ApiResult<CustomSerial>>

    @GET
    suspend fun getAcLatest(
        @Url url: String
    ): ApiResponse<ApiResult<List<AccessLatest>>>

    @GET
    suspend fun getAcHistory(
        @Url url: String,
        @Query("jsonData") jsonStr: String
    ): ApiResponse<ApiResult<List<AccessHistory>>>

    @GET
    suspend fun getAcDetail(
        @Url url: String,
        @Query("jsonData") jsonStr: String
    ): ApiResponse<ApiResult<AccessLatest>>

    @GET
    suspend fun setAcData(
        @Url url: String,
        @Query("jsonData") jsonStr: String
    ): ApiResponse<ApiResult<String>>



}