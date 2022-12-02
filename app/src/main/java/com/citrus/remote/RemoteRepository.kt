package com.citrus.remote

import com.citrus.remote.vo.ApiResult
import com.citrus.remote.vo.StoreInfo
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RemoteRepository @Inject constructor(private val apiService: ApiService) : Repository {
    override suspend fun getStoreInfo(url: String) = resultFlowData(apiAction = { apiService.getStoreData(url) })

    override suspend fun getAcSerial(url: String) = resultFlowData(apiAction = { apiService.getAcSerial(url) })

    override suspend fun getAcHistory(url: String, jsonData: String) = resultFlowData(apiAction = { apiService.getAcHistory(url, jsonData) })

    override suspend fun setAcData(url: String, jsonData: String) = resultFlowData(apiAction = { apiService.setAcData(url, jsonData) })

    override suspend fun getAcLatest(url: String) = resultFlowData(apiAction = { apiService.getAcLatest(url) })

    override suspend fun getAcDetail(url: String, jsonData: String) = resultFlowData(apiAction = { apiService.getAcDetail(url, jsonData) })
}