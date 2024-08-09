package com.bitat.utils


import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import com.bitat.Local
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import kotlin.math.abs

data class ImageParams(val width: Int, val height: Int)

object ImageUtils {

    private val regex = """x_(\d+)&y_(\d+)""".toRegex()

    fun getParamsFromUrl(url: String): ImageParams {

        val matchResult = regex.find(url)

        if (matchResult != null) {
            val (x, y) = matchResult.destructured
            return ImageParams(width = x.toInt(), height = y.toInt())
        } else {
            CuLog.error(CuTag.Base, "ImageUtils:No match found")
            return ImageParams(0, 0)
        }
    }

    fun getHeight(
        param: ImageParams,
        fixedWidth: Int,
    ): Int {
        val (x, y) = param;
        if (x == 0 || y == 0 || fixedWidth == 0) {
            return (ScreenUtils.screenWidth * 0.88 * 1.25).toInt()
        }

        if (x > y) {
            return if (abs(y / x - 9 / 16) < 0.1 || y / x < 9 / 16) {
                (0.75 * fixedWidth).toInt();
            } else if (abs(y / x - 0.75) < 0.1) {
                (0.75 * fixedWidth).toInt();
            } else {
                fixedWidth;
            }
        } else if (x == y) { // 正方形
            return fixedWidth;
        } else { // 竖图
            return if (abs(y / x - 16 / 9) < 0.22 || y / x > 16 / 9) {
                (16f / 9f * fixedWidth).toInt();
            } else if (abs(y / x - 4 / 3) < 0.1) {
                (4 / 3 * fixedWidth);
            } else {
                (4 / 3 * fixedWidth);
            }
        }
    }

    fun getParams(uri: Uri): ImageParams {
        val retriever = MediaMetadataRetriever();
        retriever.setDataSource(uri.path)

        val width =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_IMAGE_WIDTH)?.toInt() ?: 0
        val height =
            retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_IMAGE_HEIGHT)?.toInt()
                ?: 0

        retriever.release()

        return ImageParams(width = width, height = height)
    }

}

