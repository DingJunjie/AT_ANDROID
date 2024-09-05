package com.bitat.repository.po

import android.database.Cursor

class WatchHistory {
    var id :Long = 0
    var userId :Long = 0
    var kind :Int = 0
    var dataId:Long = 0
    var time:Long = 0

    companion object {
        fun of(cursor: Cursor) = WatchHistory().apply {
            id = cursor.getLong(0)
            userId = cursor.getLong(1)
            kind = cursor.getInt(2)
            dataId = cursor.getLong(3)
            time = cursor.getLong(4)
        }
    }
}