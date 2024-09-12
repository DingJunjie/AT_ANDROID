package com.bitat.repository.po

import android.database.Cursor

class WatchHistoryPo {
    var userId :Long = 0
    var dataId:Long = 0
    var kind :Int = 0
    var time:Long = 0

    companion object {
        fun of(cursor: Cursor) = WatchHistoryPo().apply {
            userId = cursor.getLong(0)
            dataId = cursor.getLong(1)
            kind = cursor.getInt(3)
            time = cursor.getLong(4)
        }
    }
}