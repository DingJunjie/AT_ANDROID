package com.bitat.repository.po

import android.database.Cursor

class NoticePo {
   var id :Long = 0
   var userId :Long = 0
   var kind :Int = 0
   var blogId :Long = 0
   var fromId :Long = 0
   var comment :String = ""
   var createTime :Long = 0

    companion object {
        fun of(cursor: Cursor) = NoticePo().apply {
            id = cursor.getLong(0)
            userId = cursor.getLong(1)
            kind = cursor.getInt(2)
            blogId = cursor.getLong(3)
            fromId = cursor.getLong(4)
            comment = cursor.getString(5)
            createTime = cursor.getLong(6)
        }
    }
}