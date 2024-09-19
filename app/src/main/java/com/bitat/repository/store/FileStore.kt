package com.bitat.repository.store

import android.content.Context
import com.bitat.MainCo
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.File

object FileStore {
    var cacheFolder: String = ""
    var cacheImageFolder: String = ""
    var cacheAudioFolder: String = ""

    fun initFolder(context: Context) {
        MainCo.launch(IO) {
            cacheFolder = context.cacheDir.absolutePath
            val imageFolderPath = "$cacheFolder/images"
            val imageFolder = File(imageFolderPath)
            val audioFolderPath = "$cacheFolder/audios"
            val audioFolder = File(audioFolderPath)
            if (!imageFolder.exists()) {
                val result = imageFolder.mkdir()
                if (!result) {
                    return@launch
                }
            }
            if(!audioFolder.exists()) {
                val result = audioFolder.mkdir()
                if(!result) {
                    return@launch
                }
            }
            cacheAudioFolder = audioFolderPath
            cacheImageFolder = imageFolderPath
        }
    }
}