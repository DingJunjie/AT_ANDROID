package com.bitat.repository.po

import android.database.Cursor

class SingleMsgPo {
    var id :Long = 0
    var selfId: Long = 0
    var otherId: Long =0
    var time: Long =0
    var status: Short =0
    var kind: Short =0
    var content: String =""

    companion object {
        fun of(cursor: Cursor) = SingleMsgPo().apply {
            id = cursor.getLong(0)
            selfId = cursor.getLong(1)
            otherId = cursor.getLong(2)
            time = cursor.getLong(3)
            status = cursor.getShort(4)
            kind = cursor.getShort(5)
            content = cursor.getString(6)
        }
    }
}