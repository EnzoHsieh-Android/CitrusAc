package com.citrus.citrusac.present.current

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citrus.remote.RemoteRepository
import com.citrus.remote.Resource
import com.citrus.remote.vo.AccessHistory
import com.citrus.remote.vo.AccessHistoryRequest
import com.citrus.remote.vo.AccessLatest
import com.citrus.util.Constants
import com.citrus.util.Constants.getServerIP
import com.citrus.util.MoshiUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CurrentViewModel @Inject constructor(private val remoteRepository: RemoteRepository) :
    ViewModel() {

    private var fetchJob: Job? = null

    private val _acSerial = MutableStateFlow("")
    val acSerial: StateFlow<String> = _acSerial

    private val _acLatest = MutableSharedFlow<Resource<List<AccessLatest>>>()
    val acLatest: SharedFlow<Resource<List<AccessLatest>>> = _acLatest

    var focusId = ""


    fun init() {
        startFetchJob()
    }


    private fun createFetchJob(): Flow<Job> = flow {
        while (true) {
            delay(2000)
            getAcSerial()
        }
    }

    private fun startFetchJob() {
        fetchJob = viewModelScope.launch {
            createFetchJob().collect()
        }
    }

    fun stopFetchJob() {
        fetchJob?.cancel()
    }


    private fun getAcSerial() = viewModelScope.launch {
        remoteRepository.getAcSerial(getServerIP() + Constants.GET_SERIAL).collectLatest {
            when (it) {
                is Resource.Success -> {
                    _acSerial.emit(it.data.custserial)
                }
                is Resource.Error -> Unit
                is Resource.Loading -> Unit
            }
        }
    }

    fun getRecordLatest() = viewModelScope.launch {
        remoteRepository.getAcLatest(getServerIP() + Constants.GET_RECORD_LATEST).collectLatest {
            _acLatest.emit(it)
        }
    }


}