package com.citrus.remote


import com.citrus.remote.vo.*
import kotlinx.coroutines.flow.Flow

interface Repository {
    suspend fun getStoreInfo(url: String): Flow<Resource<StoreInfo>>
    suspend fun getAcSerial(url: String): Flow<Resource<CustomSerial>>
    suspend fun getAcHistory(url: String, jsonData: String): Flow<Resource<List<AccessHistory>>>
    suspend fun setAcData(url: String, jsonData: String): Flow<Resource<String>>
    suspend fun getAcLatest(url: String): Flow<Resource<List<AccessLatest>>>
    suspend fun getAcDetail(url: String, jsonData: String): Flow<Resource<AccessLatest>>
    suspend fun getMemberMemo(url: String, jsonData: String): Flow<Resource<List<NoteData>>>
    suspend fun getMemberRes(url: String, jsonData: String): Flow<Resource<List<Reservation>>>
    suspend fun setMemoValid(url: String, jsonData: String): Flow<Resource<Int>>
}