package com.bitat.repository.sqlDB

import android.database.sqlite.SQLiteDatabase


private const val CREATE_TABLE_WATCH_HISTORIES = """
CREATE TABLE IF NOT  EXISTS "watch_historgty" (
  "kind" integer NOT NULL,
  "data_id" integer NOT NULL,
  "time" integer NOT NULL,
  PRIMARY KEY ("kind", "data_id")
);
"""
object WatchHistoryDB {
    fun init(db: SQLiteDatabase) = db.execSQL(CREATE_TABLE_WATCH_HISTORIES)
}