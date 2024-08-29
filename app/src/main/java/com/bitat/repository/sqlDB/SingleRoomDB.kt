package com.bitat.repository.sqlDB

import android.database.sqlite.SQLiteDatabase
import com.bitat.repository.po.SingleMsgPo
import com.bitat.repository.po.SingleRoomPo

private const val CREATE_TABLE_SINGLE_ROOM = """
CREATE TABLE IF NOT  EXISTS "single_room" (
  "self_id" INTEGER NOT NULL,
  "other_id" INTEGER NOT NULL,
  "unreads" integer NOT NULL,
  "top" integer NOT NULL,
  "cfg" TEXT NOT NULL,
  PRIMARY KEY ("self_id", "other_id")
);
"""

object SingleRoomDB {
    fun init(db: SQLiteDatabase) = db.execSQL(CREATE_TABLE_SINGLE_ROOM)

    //获取消息和聊天室信息
    fun getMagAndRoom(selfId: Long) = SqlDB.queryBatch(
        SingleRoomPo::of, """SELECT sm.*,sr.unreads,sr.top
                FROM single_msg sm
	            LEFT JOIN single_room sr ON sm.self_id = sr.self_id 
	            AND sm.other_id = sr.other_id 
                WHERE sm.self_id = ? AND ( sm.other_id, sm.time ) IN 
                ( SELECT other_id, MAX( time  ) FROM single_msg WHERE self_id = ? GROUP BY other_id ) 
                ORDER BY sm.time""", selfId, selfId
    )

    //获取聊天室详情信息
    fun getRoom(selfId: Long, otherId: Long) = SqlDB.queryOne(
        SingleRoomPo::of,
        "select * from single_room where self_id = ? and other_id = ?",
        selfId,
        otherId
    )

    //新增聊天室存在则修改
    fun insertOrUpdate(
        selfId: Long, otherId: Long, unreads: Int, top: Int, cfg: String
    ) = SqlDB.exec(
        """insert into single_room (self_id,other_id,unreads,top,cfg) values (?,?,?,?,?) 
           ON CONFLICT(self_id,other_id) DO UPDATE SET unreads = unreads + ?;""",
        selfId,
        otherId,
        unreads,
        top,
        cfg,
        unreads,
    )

    //修改配置
    fun updateUnread(unreads: Int, selfId: Long, otherId: Long) = SqlDB.exec(
        "update single_room set unreads = unreads + ? where self_id = ? and other_id = ?",
        unreads,
        selfId,
        otherId
    )

    //修改配置
    fun updateCfg(cfg: String, selfId: Long, otherId: Long) = SqlDB.exec(
        "update single_room set cfg = ? where self_id = ? and other_id = ?",
        cfg,
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

    //删除聊天室
    fun delete(selfId: Long, otherId: Long) = SqlDB.exec(
        "delete from single_room where self_id = ? and other_id = ?",
        selfId,
        otherId,
    )

}
