package com.bitat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent

/**
 * Created by ssk on 2022/4/17.
 */
object AppConfig {
    const val BASE_URL = ""
    const val APP_DESIGN_WIDTH = 750
}

@SuppressLint("StaticFieldLeak")
object Local {
    var ctx: Context? = null

    var mainAct:MainActivity?=null

    fun navigateToLoginPage() {
        mainAct?.let { context->
            val intent = Intent(context, LogoutActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context.startActivity(intent)
        }

    }
}