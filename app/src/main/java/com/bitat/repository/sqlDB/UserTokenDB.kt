package com.bitat.repository.sqlDB

import com.bitat.repository.po.SingleMsgPo
import com.bitat.repository.po.UserTokenPo
import org.sqlite.database.sqlite.SQLiteDatabase

/**
 *    author : shilu
 *    date   : 2024/9/4  14:35
 *    desc   :
 */
private const val CREATE_TABLE_TOKEN = """
CREATE TABLE IF NOT EXISTS "user_token" (
  "user_id" INTEGER NOT NULL,
  "token" TEXT NOT NULL,
  "label" TEXT NOT NULL,
  "expire" INTEGER NOT NULL,
  "update_time" INTEGER NOT NULL,
  PRIMARY KEY ("user_id")
);"""

object UserTokenDB {
    fun init(db: SQLiteDatabase) = db.execSQL(CREATE_TABLE_TOKEN)

    //插入
    fun saveOne(po:UserTokenPo) =
        SqlDB.exec(
            """insert into user_token (user_id,token,label,expire,update_time) values (?,?,?,?,?)
             ON CONFLICT(user_id) DO UPDATE SET token = ?,label = ?,expire = ?, update_time = ?;""",
            po.userId,
            po.token,
            po.label,
            po.expire,
            po.updateTime,
            po.token,
            po.label,
            po.expire,
            po.updateTime,
        )

    //修改
    fun updateToken(userId: Long, token: String, label: String, expire: Long, updateTime: Long) =
        SqlDB.exec("update user_token set token = ?,label = ?,expire=?,update_time = ? where user_id = ?",
            token,
            label,
            expire,
            updateTime,
            userId)


    fun updateExpire(userId: Long, expire: Long, updateTime: Long) =
        SqlDB.exec("update user_token set expire = ? ,update_time = ? where user_id = ?",
            expire,
            updateTime,
            userId)

    //查询
    //获取我对于某个用户的一条消息
    fun getToken(userId: Long) =
        SqlDB.queryOne(UserTokenPo::of, "select * from user_token where user_id = ?", userId)
}