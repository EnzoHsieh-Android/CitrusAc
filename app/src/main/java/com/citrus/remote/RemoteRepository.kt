package com.citrus.remote


import android.util.Log
import com.citrus.remote.vo.*
import com.skydoves.sandwich.suspendOnError
import com.skydoves.sandwich.suspendOnException
import com.skydoves.sandwich.suspendOnSuccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*

import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RemoteRepository @Inject constructor(private val apiService: ApiService) : Repository {

    override suspend fun setOrdersPeriodByStatus(url: String, jsonData: String) =
        flow {
            apiService.setOrdersPeriodByStatus(url, jsonData).suspendOnSuccess {
                if (data.status == 1) {
                    emit(Resource.Success(data))
                } else {
                    emit(Resource.Error(data.data))
                }
            }.suspendOnError {
                emit(Resource.Error(this.statusCode.name))
            }.suspendOnException {
                emit(Resource.Error(this.message!!))
            }
        }.onStart { emit(Resource.Loading(true)) }.onCompletion { emit(Resource.Loading(false)) }
            .flowOn(Dispatchers.IO)

    override suspend fun getAcSerial(url: String): Flow<Resource<CustomSerial>> =
        flow {
            apiService.getAcSerial(url).suspendOnSuccess {
                if (data.status == 1) {
                    emit(Resource.Success(data.data))
                } else {
                    emit(Resource.Error("Request Failed"))
                }
            }.suspendOnError {
                emit(Resource.Error(this.statusCode.name))
            }.suspendOnException {
                emit(Resource.Error(this.message!!))
            }
        }

    override suspend fun getAcHistory(
        url: String,
        jsonData: String
    ): Flow<Resource<List<AccessHistory>>> = flow {
        apiService.getAcHistory(url, jsonData).suspendOnSuccess {
            if (data.status == 1) {
                emit(Resource.Success(data.data))
            } else {
                emit(Resource.Error("Request Failed"))
            }
        }.suspendOnError {
            emit(Resource.Error(this.statusCode.name))
        }.suspendOnException {
            emit(Resource.Error(this.message!!))
        }
    }

    override suspend fun setAcData(
        url: String,
        jsonData: String
    ): Flow<Resource<AccessDataResult>> = flow {
        apiService.setAcData(url, jsonData).suspendOnSuccess {
            if (data.status == 1) {
                emit(Resource.Success(data))
            } else {
                emit(Resource.Error("Request Failed"))
            }
        }.suspendOnError {
            emit(Resource.Error(this.statusCode.name))
        }.suspendOnException {
            emit(Resource.Error(this.message!!))
        }
    }

    override suspend fun getAcLatest(url: String): Flow<Resource<List<AccessLatest>>> = flow {
        apiService.getAcLatest(url).suspendOnSuccess {
            if (data.status == 1) {
                emit(Resource.Success(data.data))
            } else {
                emit(Resource.Error("Request Failed"))
            }
        }.suspendOnError {
            emit(Resource.Error(this.statusCode.name))
        }.suspendOnException {
            emit(Resource.Error(this.message!!))
        }
    }

    override suspend fun getAcDetail(url: String, jsonData: String): Flow<Resource<AccessLatest>> =
        flow {
            apiService.getAcDetail(url, jsonData).suspendOnSuccess {
                if (data.status == 1) {
                    emit(Resource.Success(data.data))
                } else {
                    emit(Resource.Error("Request Failed"))
                }
            }.suspendOnError {
                emit(Resource.Error(this.statusCode.name))
            }.suspendOnException {
                emit(Resource.Error(this.message!!))
            }


        }
}