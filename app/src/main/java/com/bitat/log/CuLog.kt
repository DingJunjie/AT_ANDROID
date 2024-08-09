package com.bitat.log

import android.util.Log

enum class CuTag {
    SingleChat, GroupChat, Publish, Blog, Profile, Login, Base,
}

object CuLog {

    private const val TAG_PREFIX = "---BIT_AT--->"

    const val DEBUG = 1

    const val INFO = 2

    const val ERROR = 3

    var level = DEBUG


    fun debug(tag: CuTag, msg: String, err: Throwable? = null) {
        if (level <= DEBUG) Log.d("$TAG_PREFIX$tag", msg, err)
    }

    fun info(tag: CuTag, msg: String) {
        if (level <= INFO) Log.i("$TAG_PREFIX$tag", msg)
    }

    fun error(tag: CuTag, msg: String, err: Throwable? = null) {
        if (level <= ERROR) Log.e("$TAG_PREFIX$tag", msg)
    }
}

