package com.bitat.repository.po

import android.database.Cursor

class SingleRoomPo {
    var selfId: Long = 0
    var otherId: Long =0
    var time: Long =0
    var status: Short =0
    var kind: Short =0
    var content: String =""
    var unreads :Int = 0
    var top :Int = 0

    companion object {
        fun of(cursor: Cursor) = SingleRoomPo().apply {
            selfId = cursor.getLong(0)
            otherId = cursor.getLong(1)
            unreads = cursor.getInt(2)
            top = cursor.getInt(3)
            time = cursor.getLong(4)
            kind = cursor.getShort(5)
            content = cursor.getString(6)
        }
    }
}