package com.bitat.repository.sqlDB

import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.po.SingleMsgPo
import org.sqlite.database.sqlite.SQLiteDatabase

private const val CREATE_TABLE_SINGLE_MSG = """
 CREATE TABLE IF NOT EXISTS "single_msg" (
    "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
      "self_id" INTEGER NOT NULL,
      "other_id" INTEGER NOT NULL,
      "time" INTEGER NOT NULL,
      "status" INTEGER NOT NULL,
      "kind" INTEGER NOT NULL,
      "content" TEXT NOT NULL
    );

CREATE INDEX IF NOT EXISTS "single_msg_0"
ON "single_msg" (
  "self_id" ASC
);

CREATE INDEX  IF NOT EXISTS "single_msg_1"
ON "single_msg" (
  "other_id" ASC
);

CREATE INDEX  IF NOT EXISTS "single_msg_2"
ON "single_msg" (
  "time" ASC
);
"""


object SingleMsgDB {
    fun init(db: SQLiteDatabase) = db.execSQL(CREATE_TABLE_SINGLE_MSG)

    //获取我对于某个用户的多条消息
    fun findMsg(selfId: Long, otherId: Long, pageNo: Int = 0, pageSize: Int = 30) =
        SqlDB.queryBatch(
            SingleMsgPo::of,
            "select * from single_msg where self_id = ? and other_id = ?  order by time desc limit ? offset ?",
            selfId,
            otherId,
            pageSize,
            pageNo * pageSize
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
    ) = SqlDB.writeQueryOne(
        ::toLong,
        """insert into single_msg (self_id,other_id,time,status,kind,content)
                values (?,?,?,?,?,?) RETURNING last_insert_rowid() as id;""",
        selfId,
        otherId,
        time,
        status,
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

    //修改消息状态
    fun updateKind(kind: Short, selfId: Long, otherId: Long, time: Long) = SqlDB.exec(
        "update single_msg set kind = ? where self_id = ? and other_id = ? and time = ?",
        kind,
        selfId,
        otherId,
        time
    )

    fun delete(id:Long) = SqlDB.exec(
        "delete from single_msg where id = ?",
        id
    )


    //删除一条消息
    fun deleteByTime(selfId: Long, otherId: Long, time: Long) = SqlDB.exec(
        "delete from single_msg where self_id = ? and other_id = ?  and time = ?",
        selfId,
        otherId,
        time
    )

    // 清空消息
    fun clear(selfId: Long, otherId: Long) = SqlDB.exec(
        "delete from single_msg where self_id = ? and other_id = ? ",
        selfId,
        otherId
    )
}

