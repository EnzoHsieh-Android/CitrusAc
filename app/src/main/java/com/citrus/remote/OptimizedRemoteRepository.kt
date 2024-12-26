package com.citrus.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OptimizedRemoteRepository @Inject constructor(
    private val apiService: ApiService,
    private val cache: ApiCache? = null
) : Repository {
    companion object {
        private const val TAG = "OptimizedRemoteRepository"
        private const val MAX_RETRY_TIMES = 3
    }

    override suspend fun getStoreInfo(url: String) = safeApiCall {
        apiService.getStoreData(buildUrl(url))
    }

    override suspend fun getAcSerial(url: String) = safeApiCall {
        apiService.getAcSerial(buildUrl(url))
    }

    override suspend fun getAcHistory(url: String, jsonData: String) = safeApiCall {
        validateJsonData(jsonData)
        apiService.getAcHistory(buildUrl(url), jsonData)
    }

    override suspend fun setAcData(url: String, jsonData: String) = safeApiCall {
        validateJsonData(jsonData)
        apiService.setAcData(buildUrl(url), jsonData)
    }

    override suspend fun getAcLatest(url: String) = safeApiCall {
        apiService.getAcLatest(buildUrl(url))
    }

    override suspend fun getAcDetail(url: String, jsonData: String) = safeApiCall {
        validateJsonData(jsonData)
        apiService.getAcDetail(buildUrl(url), jsonData)
    }

    override suspend fun getMemberMemo(url: String, jsonData: String) = safeApiCall {
        validateJsonData(jsonData)
        apiService.getMemberMemo(buildUrl(url), jsonData)
    }

    override suspend fun getMemberRes(url: String, jsonData: String) = safeApiCall {
        validateJsonData(jsonData)
        apiService.getMemberRes(buildUrl(url), jsonData)
    }

    override suspend fun setMemoValid(url: String, jsonData: String) = safeApiCall {
        validateJsonData(jsonData)
        apiService.setMemoValid(buildUrl(url), jsonData)
    }

    private suspend fun <T> safeApiCall(
        action: suspend () -> T
    ) = withContext(Dispatchers.IO) {
        try {
            executeWithRetry { resultFlowData(apiAction = { action() }) }
        } catch (e: Exception) {
            logError(e)
            throw e
        }
    }

    private suspend fun <T> executeWithRetry(
        times: Int = MAX_RETRY_TIMES,
        initialDelay: Long = 100,
        maxDelay: Long = 1000,
        factor: Double = 2.0,
        block: suspend () -> T
    ): T {
        var currentDelay = initialDelay
        repeat(times - 1) { attempt ->
            try {
                return block()
            } catch (e: Exception) {
                logError(e, attempt)
            }
            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong().coerceAtMost(maxDelay)
        }
        return block()
    }

    private fun buildUrl(endpoint: String): String {
        return endpoint.trim().apply {
            logApiCall("Building URL", this)
        }
    }

    private fun validateJsonData(jsonData: String) {
        require(jsonData.isNotEmpty()) { "JSON data cannot be empty" }
    }

    private fun logApiCall(action: String, url: String) {
        Log.d(TAG, "API Call - $action: $url")
    }

    private fun logError(error: Exception, attempt: Int = 0) {
        Log.e(TAG, "Error on attempt $attempt: ${error.message}", error)
    }
}