package com.bitat.utils

import android.media.MediaMetadataRetriever
import android.net.Uri
import com.bitat.Local

data class VideoParams(val width: Int, val height: Int, val duration: Int)

class VideoUtils {

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

}