package com.citrus.citrusac.present.history

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.citrus.remote.RemoteRepository
import com.citrus.remote.Resource
import com.citrus.remote.vo.AcDetailRequest
import com.citrus.remote.vo.AccessHistory
import com.citrus.remote.vo.AccessHistoryRequest
import com.citrus.remote.vo.AccessLatest
import com.citrus.util.Constants
import com.citrus.util.Constants.getCurrentDate
import com.citrus.util.Constants.getCurrentTime
import com.citrus.util.Constants.getServerIP
import com.citrus.util.MoshiUtil
import com.citrus.util.ext.fineEmit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject


sealed class UiAction {
    data class SearchDate(val dates: List<String>) : UiAction()
    data class SearchStr(val queryStr: String) : UiAction()
}

@HiltViewModel
class HistoryViewModel @Inject constructor(private val remoteRepository: RemoteRepository) :
    ViewModel() {

    private val _acHistory = MutableSharedFlow<Resource<List<AccessHistory>>>()
    val acHistory: SharedFlow<Resource<List<AccessHistory>>> = _acHistory

    private val _acDetail = MutableSharedFlow<Resource<AccessLatest>>()
    val acDetail: SharedFlow<Resource<AccessLatest>> = _acDetail

    private val _acHistoryEmpty = MutableSharedFlow<Boolean>()
    val acHistoryEmpty: SharedFlow<Boolean> = _acHistoryEmpty

    val accept: (UiAction) -> Unit
    private val actionSharedFlow = MutableSharedFlow<UiAction>()

    private val dateSearch = actionSharedFlow
        .filterIsInstance<UiAction.SearchDate>()
        .distinctUntilChanged()
        .onStart { emit(UiAction.SearchDate(listOf(getCurrentDate(), getCurrentDate()))) }

    private val querySearch = actionSharedFlow
        .filterIsInstance<UiAction.SearchStr>()
        .distinctUntilChanged()
        .onStart { emit(UiAction.SearchStr("")) }


    init {
        accept = { action ->
            viewModelScope.launch { actionSharedFlow.emit(action) }
        }

        viewModelScope.launch {
            combineTransform(dateSearch, querySearch) { dates, query ->
                if (dates.dates[0] == "" && dates.dates[1] == "" && query.queryStr == "") {
                    _acHistoryEmpty.fineEmit(true)
                    return@combineTransform
                } else {
                    getAcHistory(dates.dates.first(), dates.dates.last(), query.queryStr)
                }

                emit("")
            }.collect()
        }

    }


    private fun getAcHistory(startDate: String, endDate: String, query: String) =
        viewModelScope.launch {
            val accessHistoryRequest = AccessHistoryRequest(
                logTimeStart = startDate,
                logTimeEnd = endDate,
                queryStr = query
            )
            remoteRepository.getAcHistory(
                getServerIP() + Constants.GET_RECORD_HISTORY,
                MoshiUtil.toJson(accessHistoryRequest)
            ).collectLatest {
                _acHistoryEmpty.fineEmit(false)
                _acHistory.fineEmit(it)
            }
        }

    fun getAcDetail(no: String) = viewModelScope.launch {
        val acDetailRequest = AcDetailRequest(
            custNo = no
        )
        remoteRepository.getAcDetail(
            getServerIP() + Constants.GET_MEMBER_DETAIL,
            MoshiUtil.toJson(acDetailRequest)
        ).collectLatest {
            _acDetail.fineEmit(it)
        }
    }

    fun setDataEmpty() = viewModelScope.launch {
        _acHistoryEmpty.fineEmit(true)
    }


}