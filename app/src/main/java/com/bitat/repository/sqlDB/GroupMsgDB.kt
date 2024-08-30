package com.bitat.repository.sqlDB

import org.sqlite.database.sqlite.SQLiteDatabase

private const val CREATE_TABLE_GROUP_MSG = """
CREATE TABLE IF NOT  EXISTS "group_msg" (
  "self_id" INTEGER NOT NULL,
  "room_id" INTEGER NOT NULL,
  "seq" integer NOT NULL,
  "from_id" INTEGER NOT NULL,
  "status" integer NOT NULL,
  "time" INTEGER NOT NULL,
  "kind" TEXT NOT NULL,
  "content" TEXT NOT NULL,
  PRIMARY KEY ("self_id", "room_id")
);

CREATE UNIQUE INDEX "group_msg_0"
ON "group_msg" (
  "seq" ASC
);
"""

object GroupMsgDB{
    fun init(db: SQLiteDatabase) = db.execSQL(CREATE_TABLE_GROUP_MSG)
}