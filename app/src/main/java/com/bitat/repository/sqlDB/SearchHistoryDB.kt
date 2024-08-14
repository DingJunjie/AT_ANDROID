package com.bitat.repository.sqlDB

import android.database.sqlite.SQLiteDatabase

private const val CREATE_TABLE_SEARCH_HISTORIES = """
CREATE TABLE  IF NOT  EXISTS "search_history" (
  "user_id" INTEGER NOT NULL,
  "content" TEXT NOT NULL,
  "time" INTEGER NOT NULL,
  PRIMARY KEY ("user_id", "content")
);
"""

private const val INSERT_INTO_SEARCH_HISTORIES = """
    INSERT INTO "search_history" VALUES (?, ?, ?)
"""

private const val QUERY_SEARCH_HISTORIES = """
    SELECT * FROM "search_history" ORDER BY time DESC
"""

object SearchHistoryDB {
    fun init(db: SQLiteDatabase) = db.execSQL(CREATE_TABLE_SEARCH_HISTORIES)

    fun insert(db: SQLiteDatabase, userId: Long, content: String, time: Long) = db.execSQL(
        INSERT_INTO_SEARCH_HISTORIES, arrayOf(userId, content, time)
    )
}