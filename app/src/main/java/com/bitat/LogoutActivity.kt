package com.bitat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bitat.ui.theme.BitComposeTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


/**
 *    author : shilu
 *    date   : 2024/9/2  16:47
 *    desc   : 退出登录中转页，用于清理MainAct缓存重新创建一个全新的Main页面
 */
class LogoutActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BitComposeTheme {
                LogoutPage(this)
            }
        }
        // 注册一个返回按钮处理程序
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 不响应系统返回事件

            }
        })
    }

    fun restartApp() {
        val intent = Intent(this, MainActivity::class.java)
        // Intent.FLAG_ACTIVITY_NEW_TASK
            //描述: 这个标志告诉系统将 Activity 作为一个新的任务（Task）启动，而不是在现有的任务中启动。这意味着，新的 Activity 会被放在一个新的任务栈中，独立于当前的任务栈。
            //如果新的任务已经存在，那么新启动的 Activity 会被放入现有任务的栈顶。
            //如果新的任务不存在，系统将创建一个新的任务，并将 Activity 放入其中。
        // Intent.FLAG_ACTIVITY_CLEAR_TASK
            // 这个标志告诉系统清除与指定任务关联的所有 Activity（即清空任务栈），然后将新启动的 Activity 作为栈中的唯一 Activity。
            //系统将清除与新启动的 Activity 关联的任务中的所有现有 Activity，然后将新启动的 Activity 放入任务中。
            //新启动的 Activity 将成为任务中的根 Activity（即任务栈中的第一个 Activity）。
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }
}

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun LogoutPage(loginAct: LogoutActivity) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(150.dp)
                    .padding(20.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        )
        Text(text = "正在清除用户数据请稍等···", style = MaterialTheme.typography.titleMedium)
        coroutineScope.launch {
            delay(1000)
            loginAct.restartApp()
        }
    }

}