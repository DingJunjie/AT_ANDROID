package com.bitat.repository.sqlDB

import org.sqlite.database.sqlite.SQLiteDatabase

private const val CREATE_TABLE_GROUP_ROOM = """
CREATE TABLE  IF NOT  EXISTS"group_room" (
  "self_id" INTEGER NOT NULL,
  "room_id" INTEGER NOT NULL,
  "members" integer NOT NULL,
  "status" integer NOT NULL,
  "unread" INTEGER NOT NULL,
  "type" integer NOT NULL,
  "name" TEXT NOT NULL,
  "cover" TEXT NOT NULL,
  "cfg" TEXT NOT NULL,
  "intro" TEXT NOT NULL,
  PRIMARY KEY ("self_id", "room_id")
);
"""

object GroupRoomDB{
    fun init(db: SQLiteDatabase) = db.execSQL(CREATE_TABLE_GROUP_ROOM)
}