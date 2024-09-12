package com.bitat.repository.sqlDB

import com.bitat.repository.po.SocialRelPo
import org.sqlite.database.sqlite.SQLiteDatabase

private const val CREATE_TABLE_SOCIAL_REL= """
CREATE TABLE IF NOT  EXISTS "social_rel" (
  "self_id" INTEGER NOT NULL,
  "other_id" INTEGER NOT NULL,
  "rel" integer NOT NULL,
  PRIMARY KEY ("self_id", "other_id")
);
"""
object SocialRelDB {
    fun init(db: SQLiteDatabase) = db.execSQL(CREATE_TABLE_SOCIAL_REL)
    fun insertOrUpdate(po :SocialRelPo) = SqlDB.exec(
        """insert into social_rel (self_id,other_id,rel)
                values (?,?,?) ON CONFLICT(self_id,other_id) DO UPDATE SET rel = ?;""",
        po.selfId,po.otherId,po.rel,po.rel)

    fun getRel(selfId:Long,otherId:Long) = SqlDB.queryOne(
        SocialRelPo::of,"select * from social_rel where self_id = ? and other_id = ?"
        ,selfId,otherId)
    fun  del(selfId:Long,otherId:Long) = SqlDB.exec(
        "delete from social_rel where self_id = ? and other_id = ?",
        selfId,otherId
    )
}