package com.bitat.repository.po

import android.database.Cursor

class SingleRoomPo {
    var id: Long = 0
    var selfId: Long = 0
    var otherId: Long = 0
    var time: Long = 0
    var status: Short = 0
    var kind: Short = 0
    var content: String = ""
    var unreads: Int = 0
    var top: Int = 0
    var cfg: String = ""
    // ----------------

    var nickname: String = ""
    var alias: String = ""
    var profile: String = ""
    var rel: Int = 0
    var revRel: Int = 0

    companion object {
        fun of(cursor: Cursor) = SingleRoomPo().apply {
            id = cursor.getLong(0)
            selfId = cursor.getLong(1)
            otherId = cursor.getLong(2)
            time = cursor.getLong(3)
            status = cursor.getShort(4)
            kind = cursor.getShort(5)
            content = cursor.getString(6)
            unreads = cursor.getInt(7)
            top = cursor.getInt(8)
        }

        fun ofRoom(cursor: Cursor) = SingleRoomPo().apply {
            selfId = cursor.getLong(0)
            otherId = cursor.getLong(1)
            unreads = cursor.getInt(2)
            top = cursor.getInt(3)
            cfg = cursor.getString(4)
        }
    }
}