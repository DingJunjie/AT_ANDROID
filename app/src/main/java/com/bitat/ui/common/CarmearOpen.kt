package com.bitat.ui.common

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.bitat.log.CuLog
import com.bitat.log.CuTag

/**
 *    author : shilu
 *    date   : 2024/8/8  15:34
 *    desc   :
 */

@Composable
fun CarmeraOpen() {
    var outUri: Uri?
    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
//        FileUtils.getImgCacheDir(LocalContext.current)?.let {
//            var outUri = it
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, it).apply {
//                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//            }
//        }
        CuLog.debug(CuTag.Publish, "intent获取到拍照信息${intent.data?.path}")
    }

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) {
            CuLog.debug(CuTag.Publish, "launcher获取到拍照信息${it.data?.data?.path}")
        }

    Column {
        launcher.launch(intent)
    }

}

//图片结果
class PictureResult(val uri: Uri?, val isSuccess: Boolean)

class SelectPicture : ActivityResultContract<Unit?, PictureResult>() {

    private var context: Context? = null

    override fun createIntent(context: Context, input: Unit?): Intent {
        this.context = context
        return Intent(Intent.ACTION_PICK).setType("image/*")
    }

    override fun parseResult(resultCode: Int, intent: Intent?): PictureResult {
        return PictureResult(intent?.data, resultCode == Activity.RESULT_OK)
    }
}