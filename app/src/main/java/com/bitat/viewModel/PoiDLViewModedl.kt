package com.bitat.viewModel

import androidx.lifecycle.ViewModel
import com.amap.api.services.core.PoiItemV2
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.state.PoiDLState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/**
 *    author : shilu
 *    date   : 2024/8/5  10:24
 *    desc   :
 */
class PoiDLViewModedl : ViewModel() {
    val poiDLState = MutableStateFlow(PoiDLState())

    fun searchListUpdate(list: MutableList<PoiItemV2>) {
        CuLog.debug(CuTag.Publish,"更新iopList${list.size}")
        poiDLState.update {
            it.poiList.addAll(list)
            it
        }


    }

}