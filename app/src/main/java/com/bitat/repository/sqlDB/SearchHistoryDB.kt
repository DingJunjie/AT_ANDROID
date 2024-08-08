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
object SearchHistoryDB{
    fun init(db: SQLiteDatabase) = db.execSQL(CREATE_TABLE_SEARCH_HISTORIES)
}