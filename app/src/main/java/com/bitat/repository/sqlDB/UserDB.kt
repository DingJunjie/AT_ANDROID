package com.bitat.repository.sqlDB

import org.sqlite.database.sqlite.SQLiteDatabase
import com.bitat.repository.po.UserPo


private const val CREATE_TABLE_USER = """
CREATE TABLE IF NOT EXISTS "user" (
  "user_id" INTEGER NOT NULL,
  "nickname" TEXT NOT NULL,
  "phone" TEXT NOT NULL,
  "profile" TEXT NOT NULL,
  "token" TEXT NOT NULL,
  "label" TEXT NOT NULL,
  "gender" INTEGER NOT NULL,
  "verified" INTEGER NOT NULL,
  "status" INTEGER NOT NULL,
  "fans" INTEGER NOT NULL,
  "follows" INTEGER NOT NULL,
  "expire_time" INTEGER NOT NULL,
  "login_time" INTEGER NOT NULL,
  PRIMARY KEY ("user_id")
);"""

object UserDB {

    fun init(db: SQLiteDatabase) = db.execSQL(CREATE_TABLE_USER)
    fun findById(id: Long) =SqlDB.queryBatch(UserPo::of, "select id,name,score form user where id = ?", id)
}
//
//class User {
//    var id: Long = 0
//    var name: String = ""
//    var score: Double = 0.0
//
//    companion object {
//        fun of(cursor: Cursor) = User().apply {
//            id = cursor.getLong(0)
//            name = cursor.getString(1)
//            score = cursor.getDouble(2)
//        }
//    }
//}