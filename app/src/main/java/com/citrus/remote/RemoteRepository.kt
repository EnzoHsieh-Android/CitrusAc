package com.citrus.remote

import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RemoteRepository @Inject constructor(private val apiService: ApiService) : Repository {

    override suspend fun getAcSerial(url: String) =
        resultFlowData(apiAction = { apiService.getAcSerial2(url) }, onSuccess = { result ->
            if (result.data.status != 1) {
                Resource.Error("Request Failed")
            } else {
                result.data.data?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Request Failed")
            }
        })


    override suspend fun getAcHistory(
        url: String,
        jsonData: String
    ) = resultFlowData(apiAction = { apiService.getAcHistory(url, jsonData) },
        onSuccess = { result ->
            if (result.data.status != 1) {
                Resource.Error("Request Failed")
            } else {
                result.data.data?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Data is null")

            }
        })

    override suspend fun setAcData(
        url: String,
        jsonData: String
    ) = resultFlowData(apiAction = { apiService.setAcData(url, jsonData) },
        onSuccess = { result ->
            if (result.data.status != 1) {
                Resource.Error("Request Failed")
            } else {
                result.data.data?.let {
                    Resource.Success(it)
                } ?: Resource.Error("Data is null")
            }
        })

    override suspend fun getAcLatest(url: String) =
        resultFlowData(apiAction = { apiService.getAcLatest(url) },
            onSuccess = { result ->
                if (result.data.status != 1) {
                    Resource.Error("Request Failed")
                } else {
                    result.data.data?.let {
                        Resource.Success(it)
                    } ?: Resource.Error("Data is null")
                }
            })

    override suspend fun getAcDetail(url: String, jsonData: String) =
        resultFlowData(apiAction = { apiService.getAcDetail(url, jsonData) },
            onSuccess = { result ->
                if (result.data.status != 1) {
                    Resource.Error("Request Failed")
                } else {
                    result.data.data?.let {
                        Resource.Success(it)
                    } ?: Resource.Error("Data is null")
                }
            })
}