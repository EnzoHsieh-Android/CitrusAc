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
import com.citrus.util.ext.fineEmit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
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


}