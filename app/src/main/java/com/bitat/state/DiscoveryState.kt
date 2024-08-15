package com.bitat.state

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.bitat.dto.resp.BlogBaseDto

enum class DiscoveryMenuOptions : MenuOptions {
    DISCOVERY, PODCAST, ACTIVITY;

    override fun getUiContent(): String {
        return when (this) {
            DISCOVERY ->
                "探索"

            PODCAST ->
                "播客"

            ACTIVITY ->
                "活动"
        }
    }
}

data class DiscoveryState(
    val detailList: SnapshotStateList<BlogBaseDto> = mutableStateListOf(),
    val discoveryList: SnapshotStateList<BlogBaseDto> = mutableStateListOf(),
    val discoveryHeightList: SnapshotStateList<Float> = mutableStateListOf(),

    val currentMenu: DiscoveryMenuOptions = DiscoveryMenuOptions.DISCOVERY
)