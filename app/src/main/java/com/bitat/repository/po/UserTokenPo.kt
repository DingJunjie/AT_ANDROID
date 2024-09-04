package com.bitat.repository.po

import android.database.Cursor

/**
 *    author : shilu
 *    date   : 2024/9/4  14:43
 *    desc   :
 */
class UserTokenPo {
    var userId: Long = 0
    var token: String = ""
    var label: String = ""
    var expire: Long = 0
    var updateTime:Long = 0
    companion object {
        fun of(cursor: Cursor) = UserTokenPo().apply {
            userId = cursor.getLong(0)
            token = cursor.getString(1)
            label = cursor.getString(2)
            expire = cursor.getLong(3)
            updateTime = cursor.getLong(4)
        }
    }
}