package com.citrus.remote

import android.util.Log
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
fun <T, DATA> resultFlowData(
    apiAction: suspend () -> ApiResponse<T>,
    onSuccess: (ApiResponse.Success<T>) -> Resource<DATA>
) = flow {
    apiAction().suspendOnSuccess {
        emit(onSuccess(this))
    }.suspendOnError {
        throw RetryCondition(this.statusCode.name)
    }.suspendOnException {
        throw RetryCondition(this.exception.message!!)
    }
}.retryWhen { cause, attempt ->

    val delayTime = when (attempt) {
        0L -> 3000L
        1L -> 9000L
        2L -> 15000L
        else -> 3000L
    }

    if (cause is RetryCondition && attempt < 2) {
        delay(delayTime)
        return@retryWhen true
    } else {
        emit(Resource.Error(cause.message!!))
        return@retryWhen false
    }
}.onCompletion { emit(Resource.Loading(false)) }
    .catch {
        Log.e("error", it.localizedMessage)
    }.flowOn(Dispatchers.IO)



