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

    //通过我的id查询聊天室
    fun findRoom(selfId: Long) = SqlDB.queryBatch(
        SingleRoomPo::of, """SELECT
	            sm.*,sr.unreads,sr.top
                FROM
	            single_msg sm
	            LEFT JOIN single_room sr ON sm.self_id = sr.self_id 
	            AND sm.other_id = sr.other_id 
                WHERE
	            sm.self_id = ?
                AND ( sm.other_id, sm.time ) IN ( SELECT  other_id, MAX( time  ) FROM single_msg WHERE self_id = ? GROUP BY other_id ) 
                ORDER BY sm.time""", selfId, selfId
    )

    //新增
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
    fun updateCfg(cfg: String, selfId: Long, otherId: Long,) = SqlDB.exec(
        "update single_room set cfg = ? where self_id = ? and other_id = ?",
        cfg,
        selfId,
        otherId
    )
    fun updateTop(top: Int, selfId: Long, otherId: Long) = SqlDB.exec(
        "update single_room set top = ? where self_id = ? and other_id = ?",
        top,
        selfId,
        otherId
    )
    fun delete(selfId: Long, otherId: Long, ) = SqlDB.exec(
        "delete from single_room where self_id = ? and other_id = ?",
        selfId,
        otherId,
    )
}
