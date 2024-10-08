package com.bitat.repository.sqlDB

import com.bitat.repository.po.WatchHistoryPo
import org.sqlite.database.sqlite.SQLiteDatabase


private const val CREATE_TABLE_WATCH_HISTORIES = """
CREATE TABLE IF NOT  EXISTS "watch_history" (
 "user_id" integer NOT NULL,
  "data_id" integer NOT NULL,
  "kind" integer NOT NULL,
  "time" integer NOT NULL,
  PRIMARY KEY ( "user_id","data_id","kind")
);

"""

object WatchHistoryDB {
    fun init(db: SQLiteDatabase) = db.execSQL(CREATE_TABLE_WATCH_HISTORIES)

    fun insertOne(po: WatchHistoryPo) {
        return SqlDB.execBatch {
            it.exec(
                """insert into watch_history (user_id,data_id,kind,time)
                values (?,?,?,?) ON CONFLICT(user_id,data_id,kind) DO UPDATE SET time = ?""",
                po.userId,
                po.dataId,
                po.kind,
                po.time,
                po.time
            )
            val count = it.queryOne(
                ::toLong, "select count(*) as count from watch_history where user_id = ?", po.userId
            ) ?: 0
            if (count > 3100) {
                //删除100条
                it.exec(
                    """delete from watch_history where
                (user_id,data_id,kind) in(select user_id, data_id,kind 
                from watch_history where user_id = ? order by time asc limit 100)""",
                    po.userId
                )
            }
        }
    }

    //删除
    fun delete(userId:Long,dataId:Long,kind:Short) = SqlDB.exec(
        "delete from watch_history where user_id=?,data_id=?,kind=?",userId,dataId,kind
    )

    fun find(selfId: Long, kind:Short,pageNo: Int = 0, pageSize: Int = 50) = SqlDB.queryBatch(
        WatchHistoryPo::of,
        "select * from watch_history where user_id = ? and kind=? order by time desc limit ? offset ?",
        selfId
        ,kind,
        pageSize,
        pageNo * pageSize
    )
}