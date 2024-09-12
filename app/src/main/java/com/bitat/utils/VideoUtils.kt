package com.bitat.utils

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.bitat.Local
import com.bitat.log.CuLog
import com.bitat.log.CuTag

data class VideoParams(val width: Int, val height: Int, val duration: Int)

object VideoUtils {

    fun getParams(uri: Uri): VideoParams = MediaMetadataRetriever().use {
        it.setDataSource(Local.ctx, uri)
        val width =
            it.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt() ?: 0
        val height =
            it.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt() ?: 0
        val duration =
            it.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt() ?: 0
        VideoParams(width, height, duration)
    }

    fun getCover(uri: Uri, time: Long = 0): Uri = MediaMetadataRetriever().use {
        try {
            it.setDataSource(Local.ctx, uri)
            val firstFrame = it.getFrameAtTime(time)
            it.release()
            val bmPath = saveBitmap(firstFrame!!, "")
            return Uri.parse(bmPath)
        } catch (e: Exception) {
            CuLog.error(CuTag.Publish, "video getCover ${e.message.toString()}")
            return Uri.EMPTY
        }
    }

    //    {
    //        try {
    //            val retriever = MediaMetadataRetriever();
    //            retriever.setDataSource(path)
    //            val firstFrame = retriever.getFrameAtTime(time)
    //            retriever.release()
    //
    //            println("i have a frame ${firstFrame?.width ?: "oh no width"}")
    //
    //            val bmPath = saveBitmap(firstFrame!!, path)
    //            return Uri.parse(bmPath)
    //        } catch (e: Exception) {
    //            CuLog.error(CuTag.Publish, e.message.toString())
    //            return Uri.EMPTY
    //        }
    //        //    return firstFrame
    //    }

    fun genCoverKey() {

    }

}
