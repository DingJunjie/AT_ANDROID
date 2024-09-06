package com.bitat.repository.po

import android.database.Cursor

class NoticeMsgPo {
    var id: Long = 0
    var userId: Long = 0
    var kind: Int = 0
    var sourceId: Long = 0
    var fromId: Long = 0
    var time: Long = 0
    var content: String = ""

    companion object {
        fun of(cursor: Cursor) = NoticeMsgPo().apply {
            id = cursor.getLong(0)
            userId = cursor.getLong(1)
            kind = cursor.getInt(2)
            sourceId = cursor.getLong(3)
            fromId = cursor.getLong(4)
            time = cursor.getLong(5)
            content = cursor.getString(6)
        }
    }
}