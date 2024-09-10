package com.bitat.repository.sqlDB

import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import org.sqlite.database.sqlite.SQLiteDatabase
import org.sqlite.database.sqlite.SQLiteOpenHelper
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write


const val DB_NAME = "bit_db"
const val DB_VERSION = 1

class SqlDB(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

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

        private val locker = ReentrantReadWriteLock()

        fun init(context: Context) {
            DB = SqlDB(context)
        }

        fun <T> fetchDB(writable: Boolean = false, fn: (SQLiteDatabase) -> T) =
            (DB ?: throw Exception("Null SqlDB")).run {
                if (writable) locker.write { writableDatabase.use(fn) }
                else locker.read { readableDatabase.use(fn) }
            }

        fun exec(sql: String, vararg bindings: Any) = fetchDB(true) {
            it.execSQL(sql, bindings)
        }

        fun execFn(fn: (SqlExec) -> Unit) = fetchDB(true) {
            fn(SqlExec(it))
        }

        fun <T> writeQueryOne(toFn: (Cursor) -> T, sql: String, vararg bindings: Any): T? =
            fetchDB(true) {
                it.rawQuery(sql, bindings.map(Any::toString).toTypedArray())
                    .run { if (moveToFirst()) toFn(this) else null }
            }

        fun <T> queryOne(toFn: (Cursor) -> T, sql: String, vararg bindings: Any): T? = fetchDB {
            it.rawQuery(sql, bindings.map(Any::toString).toTypedArray())
                .run { if (moveToFirst()) toFn(this) else null }
        }

        inline fun <reified T> queryBatch(
            crossinline toFn: (Cursor) -> T, sql: String, vararg bindings: Any
        ) = fetchDB {
            it.rawQuery(sql, bindings.map(Any::toString).toTypedArray()).run {
                Array(count) {
                    moveToNext()
                    toFn(this)
                }
            }
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

fun toInt(cursor: Cursor): Int = cursor.getInt(0)

fun toLong(cursor: Cursor): Long = cursor.getLong(0)

fun toDouble(cursor: Cursor): Double = cursor.getDouble(0)

fun toBlob(cursor: Cursor): ByteArray = cursor.getBlob(0)

fun toString(cursor: Cursor): String = cursor.getString(0)
