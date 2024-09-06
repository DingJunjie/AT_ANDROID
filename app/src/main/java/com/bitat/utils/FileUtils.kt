package com.bitat.utils

import android.content.Context
import android.graphics.Bitmap
import com.bitat.Local
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.file.Files
import kotlin.io.path.Path
import kotlin.math.pow


fun saveBitmap(bm: Bitmap, originPath: String): String { //    val savePath = Environment.getRootDirectory().absolutePath


    //    val savePathList = originPath.split("/")
    val savePath =
        Local.ctx!!.filesDir.absoluteFile //    val savePath = savePathList.subList(0, savePathList.size - 1).joinToString("/")
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

// 计算目录大小
fun getDirSize(file: File): Long {
    var size: Long = 0
    if (file.isDirectory) {
        file.listFiles()?.forEach { size += getDirSize(it) }
    } else {
        size = file.length()
    }
    return size
}

// 自动转换单位的格式化函数
fun formatSize(size: Long): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
    return String.format("%.2f %s", size / 1024.0.pow(digitGroups.toDouble()), units[digitGroups])
}

// 保存Bitmap到APP的缓存文件夹
fun saveBitmapToCache(context: Context, bitmap: Bitmap, fileName: String): File? {
    val cacheDir = context.cacheDir.resolve("image") // 获取缓存文件夹
    val file = File(cacheDir, fileName) // 创建文件对象
    if (!file.exists()) {
        file.mkdirs() // 创建文件夹
    }
    var fos: FileOutputStream? = null
    return try {
        fos = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos) // 压缩并保存Bitmap为PNG文件
        file // 返回文件对象
    } catch (e: IOException) {
        e.printStackTrace()
        null
    } finally {
        fos?.flush()
        fos?.close()
    }
}