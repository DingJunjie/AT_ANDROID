package com.bitat.repository.po

import android.database.Cursor

class SingleRoomPo {
    var selfId: Long = 0
    var otherId: Long = 0
    var time: Long = 0
    var status: Short = 0
    var kind: Short = 0
    var content: String = ""
    var unreads: Int = 0
    var top: Int = 0

    // ----------------

    var nickname: String = ""
    var alias: String = ""
    var profile: String = ""
    var rel: Int = 0
    var revRel: Int = 0

    companion object {
        fun of(cursor: Cursor) = SingleRoomPo().apply {
            selfId = cursor.getLong(0)
            otherId = cursor.getLong(1)
            status = cursor.getShort(2)
            time = cursor.getLong(3)
            kind = cursor.getShort(4)
            content = cursor.getString(5)
            unreads = cursor.getInt(6)
            top = cursor.getInt(7)

        }
    }
}