package com.bitat.repository.po

import android.database.Cursor

class UserPo {
    var id: Long = 0
    var name: String = ""
    var score: Double = 0.0

    companion object {
        fun of(cursor: Cursor) = UserPo().apply {
            id = cursor.getLong(0)
            name = cursor.getString(1)
            score = cursor.getDouble(2)
        }
    }
}