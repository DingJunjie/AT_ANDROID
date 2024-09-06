package com.bitat.repository.sqlDB

import com.bitat.repository.po.NoticeMsgPo
import com.bitat.repository.po.NoticeMsgUnique
import com.bitat.repository.po.SingleMsgPo
import com.bitat.repository.po.SingleMsgUnique
import org.sqlite.database.sqlite.SQLiteDatabase
private const val CREATE_TABLE_NOTICE = """
CREATE TABLE IF NOT  EXISTS "notice_msg" (
  "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "user_id" integer NOT NULL,
  "kind" integer NOT NULL,
  "source_id" integer NOT NULL,
  "from_id" integer NOT NULL,
   "time" integer NOT NULL,
  "content" TEXT NOT NULL,
);

    CREATE INDEX  IF NOT EXISTS  "notice_msg_0"
    ON "notice_msg" (
    "user_id" ASC
    );
    CREATE INDEX  IF NOT EXISTS "notice_msg_1"
    ON "notice_msg" (
    "kind" ASC
    );
    CREATE INDEX  IF NOT EXISTS "notice_msg_2"
    ON "notice_msg" (
    "source_id" ASC
    );
"""


object NoticeMsgDB {
    fun init(db: SQLiteDatabase) = db.execSQL(CREATE_TABLE_NOTICE)
    fun insertOne(po:NoticeMsgPo) :Long {
        val id = SqlDB.writeQueryOne(
            ::toLong,
            """insert into notice_msg (user_id,kind,source_id,from_id,time,content)
                values (?,?,?,?,?,?) RETURNING last_insert_rowid() as id;""",
            po.userId,
            po.kind,
            po.sourceId,
            po.fromId,
            po.time,
            po.content
        )
        val count = SqlDB.queryOne(
            ::toLong,
            "select count(*) as count from notice_msg where user_id = ?",
            po.userId,
        )?:0
       if (count>30100){
           //删除100条
           SqlDB.exec(
               """delete from notice_msg where id in(select id from notice_msg where
               user_id = ? order by time asc limit 100)""",
               po.userId
           )
       }
        return id?:0
    }

    fun insertArray(
        poList: Array<NoticeMsgPo>
    ) = SqlDB.execFn {
        for (po in poList) it.exec(
            """insert into notice_msg (user_id,kind,source_id,from_id,time,content)
                values (?,?,?,?,?,?)""",
            po.userId,
            po.kind,
            po.sourceId,
            po.fromId,
            po.time,
            po.content
        )
    }

    private fun queryUnique(
         selfId: Long,poArr: Array<NoticeMsgPo>
    ): Set<NoticeMsgUnique> = SqlDB.queryBatch(
        NoticeMsgUnique::of, """
            select kind, source_id,from_id,time from notice_msg where self_id = ?
            and (kind, source_id,from_id,time) in (${
        poArr.joinToString(",") { "(${it.kind},${it.sourceId},${it.fromId},${it.time})" }
    })""", selfId).toSet()

    fun filterDuplicate(
        selfId: Long, poArr: Array<NoticeMsgPo>
    ): Array<NoticeMsgPo> = queryUnique(selfId, poArr).run {
        poArr.filter { !contains(it.getUnique()) }.toTypedArray()
    }

    //查询通知
    fun find(selfId: Long,pageNo: Int = 0,pageSize:Int = 50) =  SqlDB.queryBatch(
        NoticeMsgPo::of,
        "select * from notice_msg where user_id = ? order by time desc limit ? offset ?",
        selfId,
        pageSize,
        pageNo * pageSize
    )
    //删除
    fun del(id:Long) = SqlDB.exec(
        """delete from notice_msg where id = ?""",
        id
    )
}