package com.bitat.repository.sqlDB

import com.bitat.repository.po.NoticeMsgPo
import org.sqlite.database.sqlite.SQLiteDatabase

private const val CREATE_TABLE_NOTICE = """
CREATE TABLE IF NOT  EXISTS "notice_msg" (
  "id" integer NOT NULL PRIMARY KEY AUTOINCREMENT,
  "user_id" integer NOT NULL,
  "kind" integer NOT NULL,
  "source_id" integer NOT NULL,
  "from_id" integer NOT NULL,
  "comment" TEXT NOT NULL,
  "create_time" integer NOT NULL
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
            """insert into notice_msg (user_id,kind,source_id,from_id,comment,create_time)
                values (?,?,?,?,?,?) RETURNING last_insert_rowid() as id;""",
            po.userId,
            po.kind,
            po.sourceId,
            po.fromId,
            po.content,
            po.time
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
               user_id = ? order by create_time asc limit 100)""",
               po.userId
           )
       }
        return id?:0
    }

    fun insertBatch(
        poList: List<NoticeMsgPo>
    ) = SqlDB.execFn {
        for (po in poList) it.exec(
            """insert into notice_msg (user_id,kind,source_id,from_id,comment,create_time)
                values (?,?,?,?,?,?)""",
            po.userId,
            po.kind,
            po.sourceId,
            po.fromId,
            po.content,
            po.time,
            po.userId
        )
    }
    //查询通知
    fun find(selfId: Long,pageNo: Int = 0,pageSize:Int = 50) =  SqlDB.queryBatch(
        NoticeMsgPo::of,
        "select * from notice_msg where user_id = ? order by create_time desc limit ? offset ?",
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