package com.bitat.repository.sqlDB

import com.bitat.repository.po.SingleMsgPo
import com.bitat.repository.po.SingleMsgUnique
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
            "select * from single_msg where self_id = ? and other_id = ? order by time desc limit ? offset ?",
            selfId,
            otherId,
            pageSize,
            pageNo * pageSize
        )

    //模糊匹配内容
    fun findContentByLike(
        selfId: Long,
        otherId: Long,
        content: String,
        pageNo: Int = 0,
        pageSize: Int = 30
    ) =
        SqlDB.queryBatch(
            SingleMsgPo::of,
            "select * from single_msg where self_id = ? and other_id = ? and content like '%${content}%' order by time desc limit ? offset ?",
            selfId,
            otherId,
            pageSize,
            pageNo * pageSize
        )

    //根据kind查询
    fun findByKind(selfId: Long, otherId: Long, kind: Int, pageNo: Int = 0, pageSize: Int = 30) =
        SqlDB.queryBatch(
            SingleMsgPo::of,
            "select * from single_msg where self_id = ? and other_id = ? and kind = ？ order by time desc limit ? offset ?",
            selfId,
            otherId,
            kind,
            pageSize,
            pageNo * pageSize
        )

    //获取我对于某个用户的一条消息
    fun getMsg(selfId: Long, otherId: Long, time: Long) = SqlDB.queryOne(
        SingleMsgPo::of,
        "select * from single_msg where self_id = ? and other_id = ? and time = ? limit 1",
        selfId,
        otherId,
        time
    )

    fun insertOneUnique(singleMsgPo: SingleMsgPo) = SqlDB.execBatch {
        val msg = getMsg(singleMsgPo.selfId, singleMsgPo.otherId, singleMsgPo.time)
        if (msg == null) {
            insertOne(singleMsgPo)
        }
    }

    //插入一条消息
    fun insertOne(
        singleMsgPo: SingleMsgPo
    ) = SqlDB.writeQueryOne(
        ::toLong,
        """insert into single_msg (self_id,other_id,time,status,kind,content)
                values (?,?,?,?,?,?) RETURNING last_insert_rowid() as id;""",
        singleMsgPo.selfId,
        singleMsgPo.otherId,
        singleMsgPo.time,
        singleMsgPo.status,
        singleMsgPo.kind,
        singleMsgPo.content
    )

    private fun queryUnique(
        selfId: Long, status: Long, poArr: Array<SingleMsgPo>
    ): Set<SingleMsgUnique> = SqlDB.queryBatch(SingleMsgUnique::of, """
            select other_id, time from single_msg where self_id = ? and status = ? 
            and (other_id, time) in (${
        poArr.joinToString(",") { "(${it.otherId},${it.time})" }
    })""", selfId, status).toSet()

    fun filterDuplicate(
        selfId: Long, status: Long, poArr: Array<SingleMsgPo>
    ): Array<SingleMsgPo> = queryUnique(selfId, status, poArr).run {
        poArr.filter { !contains(it.getUnique()) }.toTypedArray()
    }

    fun insertArray(
        poArr: Array<SingleMsgPo>
    ) = SqlDB.execBatch {
        for (po in poArr) it.exec(
            """insert into single_msg (self_id,other_id,time,status,kind,content)
                values (?,?,?,?,?,?)""",
            po.selfId,
            po.otherId,
            po.time,
            po.status,
            po.kind,
            po.content,
        )
    }

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

    fun delete(id: Long) = SqlDB.exec(
        "delete from single_msg where id = ?", id
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
        "delete from single_msg where self_id = ? and other_id = ? ", selfId, otherId
    )
}

