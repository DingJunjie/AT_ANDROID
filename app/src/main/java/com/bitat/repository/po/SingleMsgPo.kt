package com.bitat.repository.po

import android.database.Cursor

class SingleMsgPo {
    var selfId: Long = 0
    var otherId: Long =0
    var time: Long =0
    var status: Short =0
    var kind: Short =0
    var content: String =""

    companion object {
        fun of(cursor: Cursor) = SingleMsgPo().apply {
            selfId = cursor.getLong(0)
            otherId = cursor.getLong(1)
            status = cursor.getShort(2)
            time = cursor.getLong(3)
            kind = cursor.getShort(4)
            content = cursor.getString(5)
        }
    }
}