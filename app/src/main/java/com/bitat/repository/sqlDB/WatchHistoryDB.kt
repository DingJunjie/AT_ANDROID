package com.bitat.repository.sqlDB

import com.bitat.repository.po.NoticePo
import com.bitat.repository.po.WatchHistory
import org.sqlite.database.sqlite.SQLiteDatabase


private const val CREATE_TABLE_WATCH_HISTORIES = """
CREATE TABLE IF NOT  EXISTS "watch_history" (
  "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "user_id" integer NOT NULL,
  "kind" integer NOT NULL,
  "data_id" integer NOT NULL,
  "time" integer NOT NULL
);

CREATE INDEX  IF NOT EXISTS "watch_history_0"
ON "watch_history" (
  "kind" ASC
);

CREATE INDEX  IF NOT EXISTS "watch_history_1"
ON "watch_history" (
  "data_id" ASC
);
"""
object WatchHistoryDB {
    fun init(db: SQLiteDatabase) = db.execSQL(CREATE_TABLE_WATCH_HISTORIES)
    fun insertOne(selfId:Long,kind: Short, dataId: Long, time: Long) :Long {
        val id = SqlDB.writeQueryOne(
            ::toLong,
            """insert into watch_history (user_id,kind,data_id,time)
                values (?,?,?,?) RETURNING last_insert_rowid() as id;""",
            selfId,
            kind,
            dataId,
            time
        )
        val count = SqlDB.queryOne(
            ::toLong,
            "select count(*) as count from watch_history where user_id = ?"
            ,selfId
        )?:0
        if (count>30100){
            //删除100条
            SqlDB.exec(
                """delete from watch_history where
                id in(select id from watch_history where user_id = ? order by time asc limit 100)"""
                ,selfId
            )
        }
        return id?:0
    }
    //删除
    fun del(id:Long) = SqlDB.exec(
        """delete from watch_history where id = ?""",
        id
    )
    //查询通知
    fun find(selfId: Long,pageNo: Int = 0,pageSize:Int = 50) =  SqlDB.queryBatch(
        WatchHistory::of,
        "select * from watch_history where user_id = ? order by create_time desc limit ? offset ?",
        selfId,
        pageSize,
        pageNo * pageSize
    )
}