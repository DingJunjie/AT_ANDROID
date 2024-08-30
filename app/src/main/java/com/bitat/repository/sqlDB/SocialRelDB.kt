package com.bitat.repository.sqlDB

import org.sqlite.database.sqlite.SQLiteDatabase

private const val CREATE_TABLE_SOCIAL_REL= """
CREATE TABLE IF NOT  EXISTS "social_rel" (
  "self_id" INTEGER NOT NULL,
  "other_id" INTEGER NOT NULL,
  "rel" integer NOT NULL,
  "rec_msgs" integer NOT NULL,
  PRIMARY KEY ("self_id", "other_id")
);
"""
object SocialRelDB {
    fun init(db: SQLiteDatabase) = db.execSQL(CREATE_TABLE_SOCIAL_REL)
}