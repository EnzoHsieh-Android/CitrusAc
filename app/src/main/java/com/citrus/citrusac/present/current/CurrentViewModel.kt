package com.citrus.citrusac.present.current

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citrus.remote.RemoteRepository
import com.citrus.remote.Resource
import com.citrus.remote.vo.*
import com.citrus.util.Constants
import com.citrus.util.Constants.getCurrentDate
import com.citrus.util.Constants.getCurrentResDate
import com.citrus.util.Constants.getLocalIP
import com.citrus.util.Constants.getServerIP
import com.citrus.util.MoshiUtil
import com.citrus.util.ext.fineEmit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject


@HiltViewModel
class CurrentViewModel @Inject constructor(private val remoteRepository: RemoteRepository) :
    ViewModel() {

    private var fetchJob: Job? = null

    private val _acSerial = MutableStateFlow("")
    val acSerial: StateFlow<String> = _acSerial

    private val _acSerialError = MutableSharedFlow<String>()
    val acSerialError: SharedFlow<String> = _acSerialError

    private val _acLatest = MutableSharedFlow<Resource<List<AccessLatest>>>()
    val acLatest: SharedFlow<Resource<List<AccessLatest>>> = _acLatest



    private fun createFetchJob(): Flow<Job> = flow {
        while (currentCoroutineContext().isActive) {
            delay(2000)
            getAcSerial()
        }
    }

    fun startFetchJob() {
        fetchJob = viewModelScope.launch {
            createFetchJob().collect()
        }
    }

    fun stopFetchJob() {
        fetchJob?.cancel()
    }


    /**需要stateFlow的特性來達成後續接收的SerialNum沒有變化時就不觸發View collect的特性，所以在此處理Resources判斷*/
    private fun getAcSerial() = viewModelScope.launch {
        remoteRepository.getAcSerial(getLocalIP() + Constants.GET_SERIAL).collectLatest {
            when (it) {
                is Resource.Success -> {
                    _acSerial.fineEmit(it.data.custserial)
                }
                is Resource.Error -> {
                    _acSerialError.fineEmit(it.message ?: it.exception)
                }
                is Resource.Loading -> Unit
            }
        }
    }

    fun getRecordLatest() = viewModelScope.launch {
        remoteRepository.getAcLatest(getLocalIP() + Constants.GET_RECORD_LATEST).collectLatest {
            _acLatest.emit(it)
        }
    }


}