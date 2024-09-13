package com.bitat.repository.po

import android.database.Cursor
import androidx.core.database.getLongOrNull
import androidx.core.database.getShortOrNull
import androidx.core.database.getStringOrNull
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.json.Json

//@Serializable
//data class RoomCfg(
//    var background: String = "",
//    var muted: Boolean = false,
//    var isTop: Boolean = false
//)

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
    var muted: Int = 0
    var background: String = ""

    //    var cfg: String = Json.encodeToString(RoomCfg.serializer(), RoomCfg())
    var cfg: String = ""
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
            unreads = cursor.getInt(2)
            top = cursor.getInt(3)
            background = cursor.getString(4)
            muted = cursor.getInt(5)
            cfg = cursor.getString(6)

            id = cursor.getLongOrNull(7) ?: 0
            time = cursor.getLongOrNull(8) ?: 0
            status = cursor.getShortOrNull(9) ?: 0
            kind = cursor.getShortOrNull(10) ?: 0
            content = cursor.getStringOrNull(11) ?: ""
        }

        fun ofRoom(cursor: Cursor) = SingleRoomPo().apply {
            selfId = cursor.getLong(0)
            otherId = cursor.getLong(1)
            unreads = cursor.getInt(2)
            top = cursor.getInt(3)
            background = cursor.getString(4)
            muted = cursor.getInt(5)
            cfg = cursor.getString(6)

            id = cursor.getLongOrNull(7) ?: 0
        }
    }
}