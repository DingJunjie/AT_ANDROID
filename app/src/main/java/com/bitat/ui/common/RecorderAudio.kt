package com.bitat.ui.common

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.annotation.RequiresApi
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.store.FileStore
import com.bitat.utils.TimeUtils
import java.io.File
import java.io.IOException
import java.util.Timer

const val RECORDER_RATE = 44100

object RecorderAudio {
    var timer: Timer? = null
    var path = ""

    @RequiresApi(Build.VERSION_CODES.S)
    fun getRecorder(context: Context): MediaRecorder {
        val dir = File(FileStore.cacheAudioFolder)
        if (!dir.exists()) {
            dir.mkdir()
        }
        path = FileStore.cacheAudioFolder + "/" + TimeUtils.getNow().toString() + ".amr"
        val audioPath = File(path)

        val recorder = MediaRecorder(context)

        recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        recorder.setOutputFile(audioPath.absolutePath)

        return recorder
    }

    fun prepare(recorder: MediaRecorder) {
        try {
            recorder.prepare()
        } catch (e: IOException) {
            CuLog.error(CuTag.SingleChat, "audio recorder error ${e.message}")
        }
    }

    fun start(recorder: MediaRecorder) {
        try {
            recorder.start()
        } catch (e: RuntimeException) {
            CuLog.error(CuTag.SingleChat, "audio recorder start error ${e.message}")
        }
    }

    fun stop(recorder: MediaRecorder) {
        recorder.stop()
        recorder.release()
    }
}