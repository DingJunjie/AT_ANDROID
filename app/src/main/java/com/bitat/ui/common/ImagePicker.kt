package com.bitat.ui.common

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.view.Surface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import java.io.InputStream


@Composable
fun ImagePicker(onSelected: (List<Uri>) -> Unit, content: @Composable () -> Unit) {
    // Trigger the image picker
    val clickable = rememberSaveable { mutableStateOf(true) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val clipData = it.data?.clipData
            if (clipData != null) {
                onSelected((0..<clipData.itemCount).mapNotNull { i ->
                    clipData.getItemAt(i).uri
                })
            } else CuLog.error(CuTag.Publish, "Empty clipData")
        }

        clickable.value = true
    }

    val pickImageIntent = Intent(Intent.ACTION_PICK).apply {
        type = "image/* video/*"
        putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
    }


    Surface(
        modifier = Modifier.clickable {
            if (clickable.value) {
                launcher.launch(pickImageIntent)
                clickable.value = false
            }
        }
    ) {
//        Text("选择图片")
        content()
    }
}
