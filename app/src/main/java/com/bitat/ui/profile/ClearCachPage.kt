package com.bitat.ui.profile

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitat.MainCo
import com.bitat.R
import com.bitat.ext.cdp
import com.bitat.repository.sqlDB.DB_NAME
import com.bitat.ui.common.rememberToastState
import com.bitat.ui.component.CommonTopBar
import com.bitat.ui.theme.hintTextColor
import com.bitat.utils.formatSize
import com.bitat.utils.getDirSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File

/**
 *    author : shilu
 *    date   : 2024/9/5  11:14
 *    desc   :
 */

@Composable
fun ClearCachPage(navHostController: NavHostController) {
    val optList = arrayOf("临时文件", "应用数据")
    var cacheSize by remember { mutableStateOf("正在计算中···") }
    var dbSize by remember { mutableStateOf("正在计算中···") }
    fun clearCache(context: Context) {
        val cacheDir: File = context.cacheDir
        cacheDir.deleteRecursively()
    }

    fun clearDb(context: Context) {
        val cacheDir: File = context.cacheDir
        context.deleteDatabase(DB_NAME)
    }

    fun getCacheSize(context: Context) {
        val cacheDir: File = context.cacheDir
        MainCo.launch(Dispatchers.IO) {
            cacheSize = formatSize(getDirSize(cacheDir))
        }
    }

    val toast = rememberToastState()

    fun getDbSize(context: Context) {
        val dbFile: File = context.getDatabasePath(DB_NAME)
        dbSize = formatSize(if (dbFile.exists()) dbFile.length() else 0)
    }

    var showToast by remember { mutableStateOf(false) }

    val context = LocalContext.current // 获取并更新缓存大小
    LaunchedEffect(Unit) {
        getCacheSize(context)
        getDbSize(context)
    }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(topBar = {
        CommonTopBar(modifier = Modifier,
            title = stringResource(id = R.string.setting_clear_cache),
            backFn = { navHostController.popBackStack() },
            isBg = true,
            paddingStatus = true)
    }) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            optList.forEachIndexed { index, str ->

                when (index) {
                    0 -> SettingOptView(str, cacheSize, "清空") {
                        clearCache(context)
                        getCacheSize(context)
                        showToast = true
                        coroutineScope.launch {
                            delay(1500)
                            showToast = false
                        }
                    }
                    1 -> SettingOptView(str, dbSize, "清空") {
                        clearDb(context)
                        getDbSize(context)
                        showToast = true
                        coroutineScope.launch {
                            delay(1500)
                            showToast = false
                        }
                    }
                }
            }
            if (showToast) toast.show(stringResource(R.string.operation_success))
        }
    }


}

@Composable
fun SettingOptView(title: String, content: String, endText: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().height(50.dp)
        .padding(start = 30.cdp, top = 30.cdp, end = 30.cdp).clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center) {
        Text(modifier = Modifier.fillMaxWidth().fillMaxHeight().weight(1f),
            text = title,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            textAlign = TextAlign.Start)
        Text(modifier = Modifier.fillMaxWidth().fillMaxHeight().weight(2f),
            textAlign = TextAlign.Center,
            text = content,
            style = MaterialTheme.typography.bodyMedium.copy(color = hintTextColor))
        Text(modifier = Modifier.fillMaxWidth().fillMaxHeight().weight(1f),
            text = endText,
            textAlign = TextAlign.End,
            style = MaterialTheme.typography.bodyMedium)
    }
}

