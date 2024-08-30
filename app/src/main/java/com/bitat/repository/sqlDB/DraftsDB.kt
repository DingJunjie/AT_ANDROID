package com.bitat.repository.sqlDB

import org.sqlite.database.sqlite.SQLiteDatabase

private const val CREATE_TABLE_DRAFTS = """
  CREATE TABLE IF NOT  EXISTS "drafts" (
  "id" INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
  "kind" INTEGER NOT NULL,
  "music_id" INTEGER NOT NULL,
  "content" TEXT NOT NULL,
  "resource" TEXT NOT NULL,
  "tags" TEXT NOT NULL,
  "cover" TEXT NOT NULL
);
"""

object DraftsDB {
    fun init(db: SQLiteDatabase) = db.execSQL(CREATE_TABLE_DRAFTS)
}