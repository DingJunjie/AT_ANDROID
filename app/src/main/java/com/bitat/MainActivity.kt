package com.bitat

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bitat.config.BitEventBus
import com.bitat.ext.flowbus.observeEvent
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.common.KeySecret
import com.bitat.repository.singleChat.TcpClient
import com.bitat.repository.store.BaseStore
import com.bitat.repository.store.TokenStore
import com.bitat.repository.store.UserStore
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
import kotlin.system.exitProcess
import androidx.navigation.compose.rememberNavController
import com.bitat.repository.singleChat.SingleMsgHelper
import com.bitat.repository.sqlDB.SqlDB
import com.bitat.ui.common.DialogOps
import com.bitat.ui.common.DialogProps
import com.bitat.ui.common.rememberDialogState

val MainCo = MainScope()


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) //        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        //        window.setDecorFitsSystemWindows(false)
        CuLog.debug(CuTag.Login, "MainActivity----- onCreate") //设置全屏显示

        enableEdgeToEdge()
        setContent { //            SqlDB.init(this)
            Local.mainAct = this
            CuLog.level = CuLog.DEBUG
            BaseStore.init(LocalContext.current) // 设置user 到 TokenStore 中
            TokenStore.getUser()?.let {
                UserStore.initUserInfo(it)
            }
            ScreenUtils.init(LocalConfiguration.current) ////         DisposableEffect(Unit) {

            WindowCompat.setDecorFitsSystemWindows(window, false)

            // 初始化状态栏高度
            val statusBarTop = with(LocalDensity.current) {
                WindowInsetsCompat.toWindowInsetsCompat(LocalView.current.rootWindowInsets)
                    .getInsets(WindowInsetsCompat.Type.statusBars()).top.toDp()
            }
            val viewModelProvider = ViewModelProvider(this)

            statusBarHeight = statusBarTop

            val navController = rememberNavController()
            val dialogShow = remember { mutableStateOf(false) }
            val dialog = rememberDialogState()
            val dialogState = remember {
                mutableStateOf(DialogProps(title = "",
                    content = "",
                    okText = "确认",
                    cancelText = "取消",
                    okColor = Color.Black,
                    closeOnAction = false,
                    onCancel = {},
                    onOk = {
                        AtNavigation(navController).navigateToLogin()
                        dialogShow.value = false
                    }))
            }

            val navigationActions = remember(navController) {
                AtNavigation(navController)
            }
            val toastState = remember { ToastUIState() }

            BitComposeTheme {
                AppNavHost(navController,
                    AtNavigation(navController),
                    viewModelProvider = viewModelProvider)
                ToastUI(toastState)
                if (dialogShow.value) {
                    dialog.show(dialogState.value)
                }
            }

            /** toast */
            observeEvent(key = BitEventBus.ShowToast) {
                lifecycleScope.launch {
                    val data = it as ToastModel
                    toastState.show(data)
                }
            }

            /** toast */
            observeEvent(key = BitEventBus.TokenDialog) {
                lifecycleScope.launch {
                    val dialogOps = it as DialogOps
                    dialogState.value.title = dialogOps.title
                    dialogState.value.content = dialogOps.content
                    dialogState.value.closeOnAction =
                        dialogOps.closeOnAction //                    dialogState.value.onOk = dialogOps.onOk
                    //                    dialogState.value.onCancel = dialogOps.onCancel
                    dialogShow.value = true
                }
            }

            

            SingleMsgHelper.opsInit(viewModelProvider)

            KeySecret.start()
            TcpClient.start()
        }
    }

    override fun onStart() {
        super.onStart()
        CuLog.debug(CuTag.Login, "MainActivity----- onStart")
    }

    override fun onPause() {
        super.onPause()
        CuLog.debug(CuTag.Login, "MainActivity----- onPause")
    }

    //        KeySecret.start()
    //        TcpClient.start()

    override fun onResume() {
        super.onResume()

        CuLog.debug(CuTag.Login, "MainActivity----- onResume") //        MainCo= MainScope()
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
        CuLog.debug(CuTag.Login, "MainActivity----- onDestroy")
        SqlDB.close()
        MainCo.cancel()
        finishAffinity()
        android.os.Process.killProcess(android.os.Process.myPid())
        exitProcess(0)
    }
}
