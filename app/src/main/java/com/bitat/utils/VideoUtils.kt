package com.bitat.utils

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri

data class VideoParams(val width: Int, val height: Int, val duration: Int)

class VideoUtils {

    fun getParams(context: Context, uri: Uri): VideoParams = MediaMetadataRetriever().use {
        it.setDataSource(context, uri)
        val width =
            it.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)?.toInt() ?: 0
        val height =
            it.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)?.toInt() ?: 0
        val duration =
            it.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toInt() ?: 0
        VideoParams(width, height, duration)
    }

}