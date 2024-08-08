package com.bitat.repository.sqlDB

import android.database.sqlite.SQLiteDatabase

private const val CREATE_TABLE_MY_ATS = """
CREATE TABLE IF NOT  EXISTS "my_ats" (
  "self_id" INTEGER NOT NULL,
  "blog_id" INTEGER NOT NULL,
  "other_id" INTEGER NOT NULL,
  "time" INTEGER NOT NULL,
  PRIMARY KEY ("self_id", "blog_id", "other_id")
);

CREATE INDEX "index0"
ON "my_ats" (
  "other_id" ASC
);

"""

object MyAtsDB {
    fun init(db: SQLiteDatabase) = db.execSQL(CREATE_TABLE_MY_ATS)
}