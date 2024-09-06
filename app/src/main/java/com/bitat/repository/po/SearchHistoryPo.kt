package com.bitat.repository.po

import android.database.Cursor

class SearchHistoryPo {
    var userId :Long = 0
    var content: String =""
    var time: Long = 0

    companion object {
        fun of(cursor: Cursor) = SearchHistoryPo().apply {
            userId = cursor.getLong(0)
            content = cursor.getString(1)
            time = cursor.getLong(2)
        }
    }
}