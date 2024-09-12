package com.bitat.repository.po

import android.database.Cursor

class SocialRelPo {
    var selfId: Long = 0
    var otherId: Long = 0
    var rel: Short = 0

    companion object {
        fun of(cursor: Cursor) = SocialRelPo().apply {
            selfId = cursor.getLong(0)
            otherId = cursor.getLong(1)
            rel = cursor.getShort(2)
        }
    }
}