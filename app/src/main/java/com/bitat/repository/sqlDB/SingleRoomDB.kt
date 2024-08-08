package com.bitat.repository.sqlDB

import android.database.sqlite.SQLiteDatabase

private const val CREATE_TABLE_SINGLE_ROOM = """
CREATE TABLE IF NOT  EXISTS "single_room" (
  "self_id" INTEGER NOT NULL,
  "other_id" INTEGER NOT NULL,
  "unreads" integer NOT NULL,
  "cfg" TEXT NOT NULL,
  PRIMARY KEY ("self_id", "other_id")
);
"""
object SingleRoomDB {
    fun init(db: SQLiteDatabase) = db.execSQL(CREATE_TABLE_SINGLE_ROOM)
}