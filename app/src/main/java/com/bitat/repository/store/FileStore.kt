package com.bitat.repository.store

import android.content.Context
import com.bitat.MainCo
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.File

object FileStore {
    var cacheFolder: String = ""
    var cacheImageFolder: String = ""

    fun initFolder(context: Context) {
        MainCo.launch(IO) {
            cacheFolder = context.cacheDir.absolutePath
            val imageFolderPath = "$cacheFolder/images"
            val imageFolder = File(imageFolderPath)
            if (!imageFolder.exists()) {
                val result = imageFolder.mkdir()
                if (!result) {
                    return@launch
                }
            }
            cacheImageFolder = imageFolderPath
        }
    }
}