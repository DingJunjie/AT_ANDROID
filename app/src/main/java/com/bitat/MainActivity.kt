package com.bitat

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.bitat.config.BitEventBus
import com.bitat.ext.flowbus.observeEvent
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.store.BaseStore
import com.bitat.router.AppNavHost
import com.bitat.router.AtNavigation
import com.bitat.ui.common.statusBarHeight
import com.bitat.ui.theme.BitComposeTheme
import com.bitat.utils.ScreenUtils
import com.wordsfairy.note.ui.widgets.toast.ToastModel
import com.wordsfairy.note.ui.widgets.toast.ToastUI
import com.wordsfairy.note.ui.widgets.toast.ToastUIState
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

val MainCo = MainScope()


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) //        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        //        window.setDecorFitsSystemWindows(false)
        //设置全屏显示
        enableEdgeToEdge()
        setContent {
            CuLog.level = CuLog.DEBUG
            BaseStore.init(LocalContext.current) //
            // SqlDB.init(this)
            ScreenUtils.init(LocalConfiguration.current) ////         DisposableEffect(Unit) {

            // 初始化状态栏高度
            val statusBarTop = with(LocalDensity.current) {
                WindowInsetsCompat.toWindowInsetsCompat(LocalView.current.rootWindowInsets)
                    .getInsets(WindowInsetsCompat.Type.statusBars()).top.toDp()
            }
            val viewModelProvider = ViewModelProvider(this)

            statusBarHeight = statusBarTop

            val navController = rememberNavController()
            val navigationActions = remember(navController) {
                AtNavigation(navController)
            }
            val toastState = remember { ToastUIState() }

            BitComposeTheme {
                AppNavHost(navController,
                    AtNavigation(navController),
                    viewModelProvider = viewModelProvider)
                ToastUI(toastState)
            }

            /** toast */
            observeEvent(key = BitEventBus.ShowToast) {
                lifecycleScope.launch {
                    val data = it as ToastModel
                    toastState.show(data)
                }
            }
        }

//        KeySecret.start()
//        TcpClient.start()
    }

    fun getAvailableMemory(): ActivityManager.MemoryInfo {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        return ActivityManager.MemoryInfo().also { memoryInfo ->
            activityManager.getMemoryInfo(memoryInfo)
            CuLog.debug(CuTag.Blog, "系统运行内存${memoryInfo.availMem}")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        MainCo.cancel()
    }
}
