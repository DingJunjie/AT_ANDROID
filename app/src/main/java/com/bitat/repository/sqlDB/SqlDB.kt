package com.bitat.repository.sqlDB

import android.annotation.SuppressLint
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
        NoticeMsgDB.init(db)
        MyAtsDB.init(db)
        GroupRoomDB.init(db)
        GroupMsgDB.init(db)
        DraftsDB.init(db)
        UserTokenDB.init(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var DB: SqlDB? = null

        fun init(context: Context) {
            DB = SqlDB(context)
        }

        fun fetchDB(writable: Boolean = false): SQLiteDatabase =
            (DB ?: throw Exception("Null SqlDB")).run {
                if (writable) writableDatabase else readableDatabase
            }

        fun exec(sql: String, vararg bindings: Any) = fetchDB(true).use {
            it.execSQL(sql, bindings)
        }

        fun execFn(fn: (SqlExec) -> Unit) = fetchDB(true).use {
            fn(SqlExec(it))
        }

        fun <T> writeQueryOne(toFn: (Cursor) -> T, sql: String, vararg bindings: Any): T? =
            fetchDB(true).use {
                it.rawQuery(sql, bindings.map(Any::toString).toTypedArray())
                    .run { if (moveToFirst()) toFn(this) else null }
            }

        fun <T> queryOne(toFn: (Cursor) -> T, sql: String, vararg bindings: Any): T? =
            fetchDB().use {
                it.rawQuery(sql, bindings.map(Any::toString).toTypedArray())
                    .run { if (moveToFirst()) toFn(this) else null }
            }

        inline fun <reified T> queryBatch(toFn: (Cursor) -> T, sql: String, vararg bindings: Any) =
            fetchDB().use {
                it.rawQuery(sql, bindings.map(Any::toString).toTypedArray()).run {
                    Array(count) {
                        moveToNext()
                        toFn(this)
                    }
                }
            }

        fun <T> queryVersion(toFn: (Cursor) -> T, sql: String): T? = fetchDB().use {
            it.rawQuery(sql, null).run { if (moveToFirst()) toFn(this) else null }
        }
    }

}

@JvmInline
value class SqlExec(val db: SQLiteDatabase) {
    fun exec(sql: String, vararg bindings: Any) = db.execSQL(sql, bindings)
    fun <T> writeQueryOne(toFn: (Cursor) -> T, sql: String, vararg bindings: Any): T? =
        db.rawQuery(sql, bindings.map(Any::toString).toTypedArray())
            .run { if (moveToFirst()) toFn(this) else null }

}

fun toLong(cursor: Cursor): Long = cursor.getLong(0)

fun toDouble(cursor: Cursor): Double = cursor.getDouble(0)

fun toBlob(cursor: Cursor): ByteArray = cursor.getBlob(0)

fun toString(cursor: Cursor): String = cursor.getString(0)
