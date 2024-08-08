package com.bitat.repository.sqlDB

import android.database.sqlite.SQLiteDatabase

private const val CREATE_TABLE_NOTICE = """
CREATE TABLE IF NOT  EXISTS "notice" (
  "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "from_id" INTEGER NOT NULL,
  "to_id" INTEGER NOT NULL,
  "blog_id" INTEGER NOT NULL,
  "nickname" TEXT NOT NULL,
  "profile" TEXT NOT NULL,
  "content" TEXT NOT NULL,
  "time" INTEGER NOT NULL,
  "kind" INTEGER NOT NULL,
  "is_read" INTEGER NOT NULL,
  "extr_info" TEXT NOT NULL
);
"""

object NoticeDB {
    fun init(db: SQLiteDatabase) = db.execSQL(CREATE_TABLE_NOTICE)
}