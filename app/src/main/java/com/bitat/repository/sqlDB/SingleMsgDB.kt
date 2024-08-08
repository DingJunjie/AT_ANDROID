package com.bitat.repository.sqlDB

import android.database.sqlite.SQLiteDatabase

private const val CREATE_TABLE_SINGLE_MSG = """
CREATE TABLE IF NOT  EXISTS "single_msg" (
  "self_id" INTEGER NOT NULL,
  "other_id" INTEGER NOT NULL,
  "status" integer NOT NULL,
  "time" INTEGER NOT NULL,
  "kind" integer NOT NULL,
  "content" TEXT NOT NULL,
  PRIMARY KEY ("self_id", "other_id")
);

CREATE INDEX "single_msg_0"
ON "single_msg" (
  "kind" ASC
);

CREATE INDEX "single_msg_1"
ON "single_msg" (
  "time" ASC
);
"""
object SingleMsgDB {
    fun init(db: SQLiteDatabase) = db.execSQL(CREATE_TABLE_SINGLE_MSG)
}