package com.citrus.remote

import com.citrus.remote.vo.ApiResult
import com.citrus.util.Constants
import com.skydoves.sandwich.ApiResponse
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import java.lang.Exception

sealed class Resource<out T>(
    val status: Status,
    val _data: T?,
    val message: String?,
    val loading: Boolean?
) {
    data class Success<out R>(val data: R) : Resource<R>(
        status = Status.SUCCESS,
        _data = data,
        message = null,
        loading = null
    )

    data class Loading(val isLoading: Boolean) : Resource<Nothing>(
        status = Status.LOADING,
        _data = null,
        message = null,
        loading = isLoading
    )

    data class Error(val exception: String) : Resource<Nothing>(
        status = Status.ERROR,
        _data = null,
        message = exception,
        loading = null
    )
}

enum class Status {
    LOADING,
    SUCCESS,
    ERROR
}

class RetryCondition(val errorMsg: String) : Exception()

/**基於sandwich進一步封裝含retry功能、error錯誤處理,僅抽出success各自實作*/
/**crossInline：讓函數類型的參數可以被間接調用，但無法return*/
/**noInline：函數類型的參數在inline時會無法被當成對象來使用，需用noinline局部關閉inline效果*/
fun <T> resultFlowData(
    apiAction: suspend () -> ApiResponse<ApiResult<T>>,
) = flow {
    apiAction().suspendOnSuccess {
        emit(if (this.data.status != 1) {
            Resource.Error(Constants.NO_DATA)
        } else {
            this.data.data?.let {
                Resource.Success(it)
            } ?: Resource.Error("Server Error")
        })
    }.suspendOnError {
        throw RetryCondition(errorMsg = this.statusCode.name)
    }.suspendOnException {
        throw RetryCondition(errorMsg = this.exception.message!!)
    }
}.retryWhen { cause, attempt ->
    val delayTime = when (attempt) {
        0L -> 1000L
        1L -> 2000L
        2L -> 4000L
        else -> 3000L
    }

    if (cause is RetryCondition && attempt < 2) {
        delay(delayTime)
        return@retryWhen true
    } else {
        emit(Resource.Error(cause.message!!))
        return@retryWhen false
    }
}.onStart { emit(Resource.Loading(true)) }
    .onCompletion { emit(Resource.Loading(false)) }
    .catch { emit(Resource.Error(it.message ?: it.localizedMessage ?: "UnExcept Error"))
}.flowOn(Dispatchers.IO)

