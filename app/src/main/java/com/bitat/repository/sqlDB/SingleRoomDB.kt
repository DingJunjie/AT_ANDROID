package com.bitat.repository.sqlDB

import com.bitat.repository.po.SingleRoomPo
import org.sqlite.database.sqlite.SQLiteDatabase

private const val CREATE_TABLE_SINGLE_ROOM = """
CREATE TABLE IF NOT  EXISTS "single_room" (
  "self_id" INTEGER NOT NULL,
  "other_id" INTEGER NOT NULL,
  "unreads" integer NOT NULL,
  "top" integer NOT NULL,
  "background" TEXT NOT NULL,
  "muted" integer NOT NULL,
  "cfg" TEXT NOT NULL,
  PRIMARY KEY ("self_id", "other_id")
);
"""

object SingleRoomDB {
    fun init(db: SQLiteDatabase) = db.execSQL(CREATE_TABLE_SINGLE_ROOM)

    //获取消息和聊天室信息
    fun getMagAndRoom(selfId: Long) = SqlDB.queryBatch(
        SingleRoomPo::of, """SELECT sr.*, sm.id, sm.time, sm.status, sm.kind, sm.content
                                    FROM single_room sr
                                    LEFT JOIN single_msg sm 
                                    ON sr.self_id = sm.self_id
                                    AND sr.other_id = sm.other_id
                                    AND sm.time = (
                                        SELECT MAX(time)
                                        FROM single_msg
                                        WHERE self_id = sr.self_id
                                        AND other_id = sr.other_id
                                    )
                                    WHERE sr.self_id = ?
                                    ORDER BY sm.time DESC;""", selfId)

    //获取聊天室详情信息
    fun getRoom(selfId: Long, otherId: Long) = SqlDB.queryOne(
        SingleRoomPo::ofRoom,
        "select * from single_room where self_id = ? and other_id = ?",
        selfId,
        otherId
    )

    //新增聊天室存在则修改
    fun insertOrUpdate(
        po:SingleRoomPo
    ) = SqlDB.exec(
        """insert into single_room (self_id,other_id,unreads,top,background,muted,cfg) values (?,?,?,?,?,?,?) 
           ON CONFLICT(self_id,other_id) DO UPDATE SET unreads = unreads + ?;""",
        po.selfId,
        po.otherId,
        po.unreads,
        po.top,
        po.background,
        po.muted,
        po.cfg,
        po.unreads,
    )

    fun clearUnread(selfId: Long, otherId: Long) = SqlDB.exec("""
        update single_room set unreads = 0 where self_id = ? and other_id = ?
    """)

    //修改配置
    fun updateCfg(cfg: String, selfId: Long, otherId: Long) = SqlDB.exec(
        "update single_room set cfg = ? where self_id = ? and other_id = ?",
        cfg,
        selfId,
        otherId
    )

    fun updateBg(background: String, selfId: Long, otherId: Long) = SqlDB.exec(
        "update single_room set background = ? where self_id = ? and other_id = ?",
        background,
        selfId,
        otherId
    )

    //修改置顶
    fun updateTop(top: Int, selfId: Long, otherId: Long) = SqlDB.exec(
        "update single_room set top = ? where self_id = ? and other_id = ?",
        top,
        selfId,
        otherId
    )

    fun updateMuted(muted: Int, selfId: Long, otherId: Long) = SqlDB.exec(
        "update single_room set muted = ? where self_id = ? and other_id = ?",
        muted,
        selfId,
        otherId
    )

    //删除聊天室
    fun delete(selfId: Long, otherId: Long) = SqlDB.exec(
        "delete from single_room where self_id = ? and other_id = ?",
        selfId,
        otherId,
    )
    //判断当前聊天室是否存在
    fun hasRoom(selfId: Long, otherId: Long):Boolean{
        return SqlDB.queryOne(SingleRoomPo::ofRoom,
            "select * from single_room where self_id = ? and other_id = ?",
            selfId,
            otherId) != null
    }
}
