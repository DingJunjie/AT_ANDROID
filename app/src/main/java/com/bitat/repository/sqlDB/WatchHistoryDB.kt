package com.bitat.repository.sqlDB

import com.bitat.repository.po.WatchHistoryPo
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

    fun insertOne(po: WatchHistoryPo): Long {
        return SqlDB.execBatch {
            val id = it.writeQueryOne(
                ::toLong,
                """insert into watch_history (user_id,kind,data_id,time)
                values (?,?,?,?) RETURNING last_insert_rowid() as id;""",
                po.userId,
                po.kind,
                po.dataId,
                po.time
            ) ?: 0
            val count = it.queryOne(
                ::toLong, "select count(*) as count from watch_history where user_id = ?", po.userId
            ) ?: 0
            if (count > 30100) {
                //删除100条
                it.exec(
                    """delete from watch_history where
                id in(select id from watch_history where user_id = ? order by time asc limit 100)""",
                    po.userId
                )
            }
            id
        }
    }

    //删除
    fun del(id: Long) = SqlDB.exec(
        "delete from watch_history where id = ?", id
    )

    fun find(selfId: Long, pageNo: Int = 0, pageSize: Int = 50) = SqlDB.queryBatch(
        WatchHistoryPo::of,
        "select * from watch_history where user_id = ? order by time desc limit ? offset ?",
        selfId,
        pageSize,
        pageNo * pageSize
    )
}