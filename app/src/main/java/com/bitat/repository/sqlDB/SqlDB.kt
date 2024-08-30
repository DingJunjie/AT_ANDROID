package com.bitat.repository.sqlDB

import android.content.Context
import android.database.Cursor
import org.sqlite.database.sqlite.SQLiteDatabase
import org.sqlite.database.sqlite.SQLiteOpenHelper


const val DB_NAME = "bit_db"
const val DB_VERSION = 1

class SqlDB(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    /*fun init(context: Context) {
        UserDB.init(context)
        DraftsDB.init(context)
        GroupMsgDB.init(context)
        GroupRoomDB.init(context)
        MyAtsDB.init(context)
        NoticeDB.init(context)
        SearchHistoriesDB.init(context)
        SingleMsgDB.init(context)
        SingleRoomDB.init(context)
        WatchHistoriesDB.init(context)
    }*/

    override fun onCreate(db: SQLiteDatabase) {
        UserDB.init(db)
        WatchHistoryDB.init(db)
        SocialRelDB.init(db)
        SingleRoomDB.init(db)
        SingleMsgDB.init(db)
        SearchHistoryDB.init(db)
        NoticeDB.init(db)
        MyAtsDB.init(db)
        GroupRoomDB.init(db)
        GroupMsgDB.init(db)
        DraftsDB.init(db)




    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    companion object {
        private var DB: SqlDB? = null

        fun init(context: Context) {
            DB = SqlDB(context)
        }

        fun fetchDB(writable: Boolean = false): SQLiteDatabase =
            (DB ?: throw Exception("Null SqlDB")).run {
                if (writable) writableDatabase else readableDatabase
            }

        fun exec(sql: String, vararg bindings: Any) = fetchDB(true).run {
            execSQL(sql, bindings)
            close()
        }

        fun <T> writeQueryOne(toFn: (Cursor) -> T, sql: String, vararg bindings: Any): T? =
            fetchDB(true).run {
                val res = rawQuery(sql,
                    bindings.map(Any::toString)
                        .toTypedArray()).run { if (moveToFirst()) toFn(this) else null }
                close()
                res
            }

        fun <T> queryOne(toFn: (Cursor) -> T, sql: String, vararg bindings: Any): T? =
            fetchDB().run {
                val res = rawQuery(sql,
                    bindings.map(Any::toString)
                        .toTypedArray()).run { if (moveToFirst()) toFn(this) else null }
                close()
                res
            }

        inline fun <reified T> queryBatch(
            toFn: (Cursor) -> T, sql: String, vararg bindings: Any
        ) = fetchDB().run {
            val res = rawQuery(
                sql, bindings.map(Any::toString).toTypedArray()
            ).run {
                listOf(Array(count) {
                    moveToNext()
                    toFn(this)
                })
            }
            close()
            res
        }

        fun <T> queryVersion(toFn: (Cursor) -> T, sql: String): T? =
            fetchDB().run {
                val res = rawQuery(
                    sql, null
                ).run { if (moveToFirst()) toFn(this) else null }
                close()
                res
            }
    }

}
