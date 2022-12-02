package com.citrus.citrusac.present.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citrus.di.prefs
import com.citrus.remote.RemoteRepository
import com.citrus.remote.Resource
import com.citrus.remote.vo.AccessData
import com.citrus.util.Constants
import com.citrus.util.Constants.ERROR
import com.citrus.util.Constants.SUCCESS
import com.citrus.util.Constants.getServerIP
import com.citrus.util.MoshiUtil
import com.citrus.util.apkDownload.DownloadStatus
import com.citrus.util.apkDownload.downloadFile
import com.citrus.util.ext.fineEmit
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject


sealed class PageType {
    object Current : PageType()
    object History : PageType()
}


@HiltViewModel
class SharedViewModel @Inject constructor(private val remoteRepository: RemoteRepository) :
    ViewModel() {

    private val _setAcDataSuccess = MutableSharedFlow<String>()
    val setAcDataSuccess: SharedFlow<String> = _setAcDataSuccess

    private val _setPageType = MutableSharedFlow<PageType>()
    val setPageType: SharedFlow<PageType> = _setPageType

    /**更版用*/
    private var updateJob: Job? = null
    private val _downloadStatus = MutableSharedFlow<DownloadStatus>()
    val downloadStatus: SharedFlow<DownloadStatus>
        get() = _downloadStatus


    init {
        viewModelScope.launch {
            remoteRepository.getStoreInfo(url = getServerIP() + Constants.GET_STORE_DATA)
                .collectLatest {
                    when (it) {
                        is Resource.Success -> {
                            prefs.storeName = it.data.storeName
                        }
                        else -> Unit
                    }
                }
        }
    }


    fun setAcData(custNo: String, status: String = "I", memo: String = "") = viewModelScope.launch {
        remoteRepository.setAcData(
            url = getServerIP() + Constants.SET_ACCESS_DATA,
            jsonData = MoshiUtil.toJson(
                AccessData(
                    custNo = custNo,
                    deviceId = prefs.deviceId,
                    status = status,
                    memo = memo
                )
            )
        ).collectLatest {
            when (it) {
                is Resource.Success -> {
                    _setAcDataSuccess.fineEmit(SUCCESS, 2)
                }
                is Resource.Error -> {
                    _setAcDataSuccess.fineEmit(ERROR, 2)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun setViewPagerSwitch(page: PageType) = viewModelScope.launch {
        _setPageType.fineEmit(page, expectObserveCount = 2)
    }

    /**更版*/
    fun intentUpdate(file: File, url: String) {
        updateJob = viewModelScope.launch {
            HttpClient().downloadFile(file, url).collect {
                if (isActive) {
                    _downloadStatus.emit(it)
                }
            }
        }
    }


    /**取消*/
    fun cancelUpdateJob() {
        if (updateJob != null) {
            updateJob?.cancel()
        }
    }


}