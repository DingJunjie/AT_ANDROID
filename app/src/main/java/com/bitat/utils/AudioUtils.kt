package com.bitat.utils


import android.media.MediaMetadataRetriever

fun getAudioDuration(filePath: String): Int {
    val retriever = MediaMetadataRetriever()
    try {
        retriever.setDataSource(filePath)
        val durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
        return durationStr?.toInt() ?: 0
    } catch (e: Exception) {
        e.printStackTrace()
        return 0
    } finally {
        retriever.release()
    }
}