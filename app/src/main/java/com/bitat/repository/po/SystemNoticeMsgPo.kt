package com.bitat.repository.po

import android.database.Cursor

class SystemNoticeMsgPo {
    var id: Long = 0
    var userId: Long = 0
    var kind: Int = 0
    var sourceId: Long = 0
    var time: Long = 0
    var content: String = ""
    fun getUnique() = SystemNoticeMsgUnique(kind, sourceId,time)
    companion object {
        fun of(cursor: Cursor) = SystemNoticeMsgPo().apply {
            id = cursor.getLong(0)
            userId = cursor.getLong(1)
            kind = cursor.getInt(2)
            sourceId = cursor.getLong(3)
            time = cursor.getLong(4)
            content = cursor.getString(5)
        }
    }
}

data class SystemNoticeMsgUnique(
    val kind: Int, val sourceId: Long, val time: Long
) {
    companion object {
        fun of(cursor: Cursor) = SystemNoticeMsgUnique(
            cursor.getInt(0), cursor.getLong(1),
            cursor.getLong(2)
        )
    }
}