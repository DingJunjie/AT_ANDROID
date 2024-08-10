package com.bitat.ui.common

import android.app.Activity.RESULT_OK
import android.content.ContentResolver
import android.content.Intent
import android.net.Uri
import android.view.Surface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
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
import androidx.media3.common.util.Log
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import java.io.InputStream

enum class ImagePickerOption {
    SINGLE_IMAGE,
    MULTIPLE_IMAGE,
    SINGLE_VIDEO,
    SINGLE_VIDEO_MULTIPLE_IMAGE
}

@Composable
fun ImagePicker(
    onSelected: (List<Uri>) -> Unit,
    option: ImagePickerOption = ImagePickerOption.MULTIPLE_IMAGE,
    content: @Composable () -> Unit
) {
    val contentResolver= LocalContext.current.contentResolver

    // 11以上文件选择器
//    val pickMedia =
//        rememberLauncherForActivityResult(ActivityResultContracts.PickMultipleVisualMedia(2)) { uriList ->
//            // Callback is invoked after the user selects a media item or closes the
//            // photo picker.
//
//            uriList.map {
//                val type = contentResolver.getType(it)
////                CuLog.debug(CuTag.Publish, "文件类型：${type}")//查询文件类型
//            }
//
//        }

    // Trigger the image picker
    val clickable = rememberSaveable { mutableStateOf(true) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            if (option == ImagePickerOption.MULTIPLE_IMAGE) {
                val clipData = it.data?.clipData
                if (clipData != null) {
                    onSelected((0..<clipData.itemCount).mapNotNull { i ->
                        clipData.getItemAt(i).uri
                    })
                } else CuLog.error(CuTag.Publish, "Empty clipData")
            } else if (option == ImagePickerOption.SINGLE_VIDEO) {
                val uri = it.data?.data ?: Uri.EMPTY
                onSelected(listOf(uri))
            }
        }

        clickable.value = true
    }

    val pickImageIntent = Intent(Intent.ACTION_PICK).apply {
        when (option) {
            ImagePickerOption.SINGLE_IMAGE -> {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            }

            ImagePickerOption.MULTIPLE_IMAGE -> {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }

            ImagePickerOption.SINGLE_VIDEO -> {
                type = "video/*"
                putExtra(Intent.ACTION_PICK, true)
            }

            ImagePickerOption.SINGLE_VIDEO_MULTIPLE_IMAGE -> {
                type = "image/* video/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            }

        }
    }


    Surface(
        modifier = Modifier.clickable {
            if (clickable.value) {
                launcher.launch(pickImageIntent)
//                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo))
                clickable.value = false
            }
        }
    ) {
//        Text("选择图片")
        content()
    }
}
