package com.bitat.repository.store

import android.content.Context
import android.content.SharedPreferences

object BaseStore {
    private var share: SharedPreferences? = null

    fun init(context: Context) {
        if (share == null) {
            share = context.getSharedPreferences("AtStore", Context.MODE_PRIVATE)
            println("SP has init")
        }
    }

    fun getStr(key: String): String? = share?.getString(key, null)

    fun setStr(key: String, value: String) = share?.edit()?.run {
        putString(key, value)
        apply()
    }


    fun remove(key: String) = share?.edit()?.run {
        remove(key)
        apply()
    }

}