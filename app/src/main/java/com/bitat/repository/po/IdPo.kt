package com.bitat.repository.po

import android.database.Cursor

/**
 *    author : shilu
 *    date   : 2024/8/30  11:40
 *    desc   :
 */
class IdPo {
    var id :Long = 0

    companion object {
        fun of(cursor: Cursor) = SingleMsgPo().apply {
            id = cursor.getLong(0)
        }
    }
}