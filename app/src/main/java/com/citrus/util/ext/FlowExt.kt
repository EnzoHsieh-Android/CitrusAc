package com.citrus.util.ext

import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


/**StateFLow使用*/
fun <T> Fragment.lifecycleFlow(flow: Flow<T>, action: suspend (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launch {
        flow.collect { action(it) }
    }
}


/**SharedFlow使用*/
fun <T> Fragment.lifecycleLatestFlow(flow: Flow<T>, action: suspend (T) -> Unit) {
    viewLifecycleOwner.lifecycleScope.launchWhenStarted {
        flow.collectLatest(action)
    }
}

suspend fun <T> MutableSharedFlow<T>.fineEmit(obj: T, expectObserveCount: Int = 1) = run {
    do {
        val count = this.subscriptionCount.value
        if (count < expectObserveCount) {
            delay(50)
        }
    } while (count < expectObserveCount)

    this.emit(obj)
}



