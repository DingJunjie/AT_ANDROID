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
  PRIMARY KEY ("self_id", "other_id","time")
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

    //获取我对于某个用户的多条消息
    fun findMsg(selfId: Long, otherId: Long, pageSize: Int, lastTime: Long = Long.MAX_VALUE) =
        SqlDB.queryBatch(
            SingleMsgPo::of,
            "select * from single_msg where self_id = ? and other_id = ? and time < ? order by time desc limit ?",
            selfId,
            otherId,
            lastTime,
            pageSize
        )

    //获取我对于某个用户的一条消息
    fun getMsg(selfId: Long, otherId: Long) = SqlDB.queryOne(
        SingleMsgPo::of,
        "select * from single_msg where self_id = ? and other_id = ? order by time desc limit 1",
        selfId,
        otherId
    )

    //插入一条消息
    fun insertOne(
        selfId: Long, otherId: Long, status: Short, time: Long, kind: Short, content: String
    ) = SqlDB.exec(
        "insert into single_msg (self_id,other_id,status,time,kind,content) values (?,?,?,?,?,?);",
        selfId,
        otherId,
        status,
        time,
        kind,
        content
    )

    //修改消息状态
    fun updateStatus(status: Short, selfId: Long, otherId: Long, time: Long) = SqlDB.exec(
        "update single_msg set status = ? where self_id = ? and other_id = ? and time = ?",
        status,
        selfId,
        otherId,
        time
    )

    //删除一条消息
    fun delete(selfId: Long, otherId: Long, time: Long) = SqlDB.exec(
        "delete from single_msg where self_id = ? and other_id = ?  and time = ?",
        selfId,
        otherId,
        time
    )
}

