package com.bitat.utils

import android.graphics.Bitmap
import com.bitat.Local
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.file.Files
import kotlin.io.path.Path


fun saveBitmap(bm: Bitmap, originPath: String): String {
//    val savePath = Environment.getRootDirectory().absolutePath


    val savePathList = originPath.split("/")
//    val savePath = Local.ctx!!.filesDir.absoluteFile
    val savePath = savePathList.subList(0, savePathList.size - 1).joinToString("/")
    val filename = TimeUtils.getNow().toString() + ".jpg"

    val fullPath = "$savePath/$filename";

    if (Files.exists(Path(fullPath))) {
        CuLog.info(CuTag.Publish, "save exists")
        return fullPath
    } else {
        val saveFile = File(fullPath)
        saveFile.createNewFile()
        try {
            val saveImgOut = FileOutputStream(saveFile)

            bm.compress(Bitmap.CompressFormat.JPEG, 90, saveImgOut)
            saveImgOut.flush()
            saveImgOut.close()
            CuLog.info(CuTag.Publish, "save file success")
            return fullPath
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    return ""
}