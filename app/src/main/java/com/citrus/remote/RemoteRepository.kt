package com.citrus.remote

import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RemoteRepository @Inject constructor(private val apiService: ApiService) : Repository {
    override suspend fun getStoreInfo(url: String) =
        resultFlowData(apiAction = { apiService.getStoreData(url) })

    override suspend fun getAcSerial(url: String) =
        resultFlowData(apiAction = { apiService.getAcSerial(url) })

    override suspend fun getAcHistory(url: String, jsonData: String) =
        resultFlowData(apiAction = { apiService.getAcHistory(url, jsonData) })

    override suspend fun setAcData(url: String, jsonData: String) =
        resultFlowData(apiAction = { apiService.setAcData(url, jsonData) })

    override suspend fun getAcLatest(url: String) =
        resultFlowData(apiAction = { apiService.getAcLatest(url) })

    override suspend fun getAcDetail(url: String, jsonData: String) =
        resultFlowData(apiAction = { apiService.getAcDetail(url, jsonData) })

    override suspend fun getMemberMemo(url: String, jsonData: String) =
        resultFlowData(apiAction = { apiService.getMemberMemo(url, jsonData) })

    override suspend fun getMemberRes(url: String, jsonData: String) =
        resultFlowData(apiAction = { apiService.getMemberRes(url, jsonData) })

    override suspend fun setMemoValid(url: String, jsonData: String) =
        resultFlowData(apiAction = { apiService.setMemoValid(url, jsonData) })
}