package com.bitat.repository.sqlDB

import com.bitat.repository.po.SearchHistoryPo
import org.sqlite.database.sqlite.SQLiteDatabase

private const val CREATE_TABLE_SEARCH_HISTORIES = """
CREATE TABLE  IF NOT  EXISTS "search_history" (
  "user_id" INTEGER NOT NULL,
  "content" TEXT NOT NULL,
  "time" INTEGER NOT NULL,
  PRIMARY KEY ("user_id", "content")
);
"""

object SearchHistoryDB {
    fun init(db: SQLiteDatabase) = db.execSQL(CREATE_TABLE_SEARCH_HISTORIES)

    fun insertOne(po:SearchHistoryPo) {
        SqlDB.exec(
            """insert into search_history (user_id,content,time)values (?,?,?)
                ON CONFLICT(user_id,content) DO UPDATE SET user_id = ?;""",
            po.userId,
            po.content,
            po.time,
            po.userId
        )
        val count = SqlDB.queryOne(
            ::toLong,
            "select count(*) as count from search_history where user_id = ?", po.userId
        ) ?: 0
        if (count > 25) {
            //删除100条
            SqlDB.exec(
                """delete from search_history where
                time in(select time from search_history where user_id = ? order by time asc limit 5)""",
                po.userId
            )
        }
    }

    //删除
    fun delete(selfId: Long, content: String) = SqlDB.exec(
        "delete from search_history where user_id = ? and content = ?",
        selfId,
        content,
    )

    //获取我对于某个用户的多条消息
    fun find(selfId: Long) =
        SqlDB.queryBatch(
            SearchHistoryPo::of,
            "select * from search_history where user_id = ? order by time desc",
            selfId,
        )
}