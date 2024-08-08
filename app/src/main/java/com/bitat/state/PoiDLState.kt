package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.amap.api.services.core.PoiItemV2

/**
 *    author : shilu
 *    date   : 2024/8/5  10:25
 *    desc   :
 */

data class PoiDLState(val poiList: SnapshotStateList<PoiItemV2> = mutableStateListOf(), val selectPoi: PoiItemV2? = null)