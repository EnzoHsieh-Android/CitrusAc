package com.citrus.remote

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OptimizedRemoteRepository @Inject constructor(
    private val apiService: ApiService,
    private val baseUrl: String
) : Repository {
    private companion object {
        private const val TAG = "OptimizedRemoteRepository"
        private const val DEFAULT_RETRY_TIMES = 3
        private const val API_VERSION = "v1"
    }

    private val cache = ConcurrentHashMap<String, CacheEntry<Any>>()
    private val supervisor = SupervisorJob()

    override suspend fun getStoreInfo(url: String) = withContext(Dispatchers.IO) {
        executeWithRetry {
            logApiCall("GET_STORE_INFO", url)
            val fullUrl = buildUrl(url)
            resultFlowData(apiAction = { apiService.getStoreData(fullUrl) })
        }
    }

    override suspend fun getAcSerial(url: String) = withContext(Dispatchers.IO) {
        executeWithRetry {
            logApiCall("GET_AC_SERIAL", url)
            val fullUrl = buildUrl(url)
            resultFlowData(apiAction = { apiService.getAcSerial(fullUrl) })
        }
    }

    override suspend fun getAcHistory(url: String, jsonData: String) = withContext(Dispatchers.IO) {
        executeWithRetry {
            validateJsonData(jsonData)
            logApiCall("GET_AC_HISTORY", url)
            val fullUrl = buildUrl(url)
            resultFlowData(apiAction = { apiService.getAcHistory(fullUrl, jsonData) })
        }
    }

    override suspend fun setAcData(url: String, jsonData: String) = withContext(Dispatchers.IO) {
        executeWithRetry {
            validateJsonData(jsonData)
            logApiCall("SET_AC_DATA", url)
            val fullUrl = buildUrl(url)
            resultFlowData(apiAction = { apiService.setAcData(fullUrl, jsonData) })
        }
    }

    override suspend fun getAcLatest(url: String) = withContext(Dispatchers.IO) {
        executeWithRetry {
            logApiCall("GET_AC_LATEST", url)
            val fullUrl = buildUrl(url)
            resultFlowData(apiAction = { apiService.getAcLatest(fullUrl) })
        }
    }

    override suspend fun getAcDetail(url: String, jsonData: String) = withContext(Dispatchers.IO) {
        executeWithRetry {
            validateJsonData(jsonData)
            logApiCall("GET_AC_DETAIL", url)
            val fullUrl = buildUrl(url)
            resultFlowData(apiAction = { apiService.getAcDetail(fullUrl, jsonData) })
        }
    }

    override suspend fun getMemberMemo(url: String, jsonData: String) = withContext(Dispatchers.IO) {
        executeWithRetry {
            validateJsonData(jsonData)
            logApiCall("GET_MEMBER_MEMO", url)
            val fullUrl = buildUrl(url)
            resultFlowData(apiAction = { apiService.getMemberMemo(fullUrl, jsonData) })
        }
    }

    override suspend fun getMemberRes(url: String, jsonData: String) = withContext(Dispatchers.IO) {
        executeWithRetry {
            validateJsonData(jsonData)
            logApiCall("GET_MEMBER_RES", url)
            val fullUrl = buildUrl(url)
            resultFlowData(apiAction = { apiService.getMemberRes(fullUrl, jsonData) })
        }
    }

    override suspend fun setMemoValid(url: String, jsonData: String) = withContext(Dispatchers.IO) {
        executeWithRetry {
            validateJsonData(jsonData)
            logApiCall("SET_MEMO_VALID", url)
            val fullUrl = buildUrl(url)
            resultFlowData(apiAction = { apiService.setMemoValid(fullUrl, jsonData) })
        }
    }

    private suspend fun <T> executeWithRetry(
        times: Int = DEFAULT_RETRY_TIMES,
        action: suspend () -> T
    ): T {
        var lastException: Exception? = null
        repeat(times) { attempt ->
            try {
                return action()
            } catch (e: Exception) {
                lastException = e
                Log.e(TAG, "Attempt ${attempt + 1}/$times failed: ${e.message}")
            }
        }
        throw lastException ?: RuntimeException("All retry attempts failed")
    }

    private fun validateJsonData(jsonData: String) {
        require(jsonData.isNotEmpty()) { "JSON data cannot be empty" }
    }

    private fun logApiCall(method: String, url: String) {
        Log.d(TAG, "Calling $method: $url")
    }

    private fun buildUrl(endpoint: String) = "$baseUrl/$API_VERSION$endpoint"

    private data class CacheEntry<T>(
        val data: T,
        val timestamp: Long = System.currentTimeMillis()
    )
}