package com.bitat.repository.sqlDB

import android.content.Context
import android.database.Cursor
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import okhttp3.internal.closeQuietly
import org.sqlite.database.sqlite.SQLiteDatabase
import org.sqlite.database.sqlite.SQLiteOpenHelper
import java.util.concurrent.locks.ReentrantReadWriteLock
import kotlin.concurrent.read
import kotlin.concurrent.write


const val DB_NAME = "bit_db"
const val DB_VERSION = 1

class SqlDB(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        CuLog.error(CuTag.Base, "Db onCreate")
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
        private var readableOps: SqlOps? = null
        private var writableOps: SqlOps? = null

        private val locker = ReentrantReadWriteLock()

        fun init(context: Context) {
            System.loadLibrary("sqliteX")
            SqlDB(context).run {
                CuLog.error(CuTag.Base, "Init Db")
                readableOps = SqlOps(readableDatabase)
                writableOps = SqlOps(writableDatabase)
            }
        }

        fun close() {
            readableOps?.db?.closeQuietly()
            writableOps?.db?.closeQuietly()
            CuLog.debug(CuTag.Base, "关闭DB连接")
        }

        fun <T> fetchDB(writable: Boolean = false, fn: (SqlOps) -> T): T {
            val wOps = writableOps
            val rOps = readableOps
            return if (wOps != null && rOps != null) if (writable) wOps.let {
                locker.write { fn(it) }
            } else rOps.let {
                locker.read { fn(it) }
            } else throw RuntimeException("SqlDB not init")
        }

        fun exec(sql: String, vararg bindings: Any) = fetchDB(true) {
            it.exec(sql, *bindings)
        }

        fun <T> execBatch(fn: (SqlOps) -> T): T = fetchDB(true, fn)

        fun <T> writeQueryOne(toFn: (Cursor) -> T, sql: String, vararg bindings: Any): T? =
            fetchDB(true) { it.writeQueryOne(toFn, sql, *bindings) }

        fun <T> queryOne(toFn: (Cursor) -> T, sql: String, vararg bindings: Any): T? = fetchDB {
            it.queryOne(toFn, sql, *bindings)
        }

        inline fun <reified T> queryBatch(crossinline toFn: (Cursor) -> T, sql: String, vararg bindings: Any) =
            fetchDB { it.queryBatch(toFn, sql, *bindings) }

    }
}

@JvmInline
value class SqlOps(val db: SQLiteDatabase) {
    fun exec(sql: String, vararg bindings: Any) = db.execSQL(sql, bindings)
    inline fun <T> writeQueryOne(toFn: (Cursor) -> T, sql: String, vararg bindings: Any): T? =
        db.rawQuery(sql, bindings.map(Any::toString).toTypedArray())
            .run { if (moveToFirst()) toFn(this) else null }

    inline fun <T> queryOne(toFn: (Cursor) -> T, sql: String, vararg bindings: Any): T? =
        db.rawQuery(sql, bindings.map(Any::toString).toTypedArray())
            .run { if (moveToFirst()) toFn(this) else null }

    inline fun <reified T> queryBatch(toFn: (Cursor) -> T, sql: String, vararg bindings: Any): Array<T> =
        db.rawQuery(sql, bindings.map(Any::toString).toTypedArray()).run {
            Array(count) {
                moveToNext()
                toFn(this)
            }
        }

}

fun toInt(cursor: Cursor): Int = cursor.getInt(0)

fun toLong(cursor: Cursor): Long = cursor.getLong(0)

fun toDouble(cursor: Cursor): Double = cursor.getDouble(0)

fun toBlob(cursor: Cursor): ByteArray = cursor.getBlob(0)

fun toString(cursor: Cursor): String = cursor.getString(0)
