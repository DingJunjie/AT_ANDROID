package com.bitat.repository.sqlDB

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.bitat.repository.po.SingleMsgPo
import com.bitat.repository.po.UserPo

private const val CREATE_TABLE_SINGLE_MSG = """
CREATE TABLE IF NOT  EXISTS "single_msg" (
  "self_id" INTEGER NOT NULL,
  "other_id" INTEGER NOT NULL,
  "status" integer NOT NULL,
  "time" INTEGER NOT NULL,
  "kind" integer NOT NULL,
  "content" TEXT NOT NULL,
  PRIMARY KEY ("self_id", "other_id")
);

CREATE INDEX "single_msg_0"
ON "single_msg" (
  "kind" ASC
);

CREATE INDEX "single_msg_1"
ON "single_msg" (
  "time" ASC
);
"""
object SingleMsgDB {
    fun init(db: SQLiteDatabase) = db.execSQL(CREATE_TABLE_SINGLE_MSG)
    fun findMsg(selfId: Long,otherId:Long,pageSize:Byte) =SqlDB.queryBatch(SingleMsgPo::of,
        "select * form single_msg where self_id = ? and other_id = ? order by time limit ?", selfId,otherId,pageSize)
    fun getMsg(selfId: Long,otherId:Long) =SqlDB.queryOne(SingleMsgPo::of,
        "select * form single_msg where self_id = ? and other_id = ? order by time limit 1", selfId,otherId)
    fun insertOne(selfId:Long,otherId:Long,status:Short,time:Long,kind:Short,content:String) =
        SqlDB.query("insert into single_msg (self_id,other_id,status,time,kind,content) values (?,?,?,?,?,?);", selfId,otherId,status,time,kind,content)
    fun updateKind(kind:Short,selfId: Long,otherId:Long,content: String,time: Long) =
        SqlDB.query("update single_msg set kind = ? where self_id = ? and other_id = ? and content = ? and time = ?",kind,selfId,otherId,content,time)
    fun delete(selfId: Long,otherId:Long,content: String,time: Long) =
        SqlDB.query("delete from single_msg where self_id = ? and other_id = ? and content = ? and time = ?",selfId,otherId,content,time)
}

