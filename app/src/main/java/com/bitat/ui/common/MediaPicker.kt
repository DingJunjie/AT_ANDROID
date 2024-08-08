package com.bitat.ui.common

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import com.bitat.log.CuLog
import com.bitat.log.CuTag

enum class CuMType(val router: String) {
    Video("video/*"), Audio("audio/*")
}

@Composable
fun MediaPicker(type: CuMType, onSelected: (Uri) -> Unit) {
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val data = it.data?.data
            if (data != null) {
                onSelected(data)
            } else CuLog.error(CuTag.Publish, "Empty clipData")
        }
    }

    val intent = Intent(
        when (type) {
            CuMType.Video -> Intent.ACTION_PICK
            CuMType.Audio -> Intent.ACTION_GET_CONTENT
        }
    ).also {
        it.type = type.router
        it.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
    }

    // Trigger the image picker
    val clickable = rememberSaveable { mutableStateOf(true) }
    Button(onClick = {
        if (clickable.value) {
            launcher.launch(intent)
            clickable.value = false
        }
    }, enabled = clickable.value) {
        Text("选择媒体文件")
    }
}


