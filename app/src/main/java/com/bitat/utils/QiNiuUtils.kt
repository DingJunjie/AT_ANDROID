package com.bitat.utils

import android.net.Uri
import com.bitat.Local
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.UPLOAD_OPS_BLOG
import com.bitat.repository.consts.UPLOAD_OPS_PUB
import com.bitat.repository.dto.req.UploadTokenDto
import com.bitat.repository.http.auth.LoginReq
import com.bitat.repository.store.UserStore
import com.qiniu.android.common.AutoZone
import com.qiniu.android.storage.Configuration
import com.qiniu.android.storage.Configuration.RESUME_UPLOAD_VERSION_V2
import com.qiniu.android.storage.UpCompletionHandler
import com.qiniu.android.storage.UpProgressHandler
import com.qiniu.android.storage.UploadManager
import com.qiniu.android.storage.UploadOptions
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
import java.io.File
import java.util.Collections.emptyMap
import java.util.concurrent.atomic.AtomicBoolean

enum class UPLOAD_OPS {
    Blog, Pub;

    companion object {
        fun toCode(ops: UPLOAD_OPS): Byte {
            return when (ops) {
                Blog -> UPLOAD_OPS_BLOG
                Pub -> UPLOAD_OPS_PUB
            }
        }
    }
}

enum class FileType(val str: String) {
    Image("image"), Video("video"), Text("text"), Audio("audio")
}

object QiNiuUtil {

    const val QINIU_PREFIX = "http://file.bitebei.com/";
    const val QINIU_PUB_PREFIX = "https://bit-pub.bitebei.com/"

    //    const val QINIU_CHAT_PREFIX = "https://bit-chat.bitebei.com"
    const val VIDEO_COVER = "?vframe/jpg/offset/0.1/w/ww/h/hh";

    private val uploadManager = Configuration.Builder().connectTimeout(90) // 链接超时。默认90秒
        .useHttps(true) // 是否使用https上传域名
        .useConcurrentResumeUpload(true) // 使用并发上传，使用并发上传时，除最后一块大小不定外，其余每个块大小固定为4M，
        .resumeUploadVersion(RESUME_UPLOAD_VERSION_V2) // 使用新版分片上传
        .concurrentTaskCount(4) // 并发上传线程数量为3
        .responseTimeout(90) // 服务器响应超时。默认90秒
        .zone(AutoZone()) // 设置区域，不指定会默认使用 AutoZone；指定不同区域的上传域名、备用域名、备用IP。
        //.recorder(recorder) // recorder分片上传时，已上传片记录器。默认null
        //.recorder(recorder, keyGen) // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
        .build().let(::UploadManager)

    fun uploadFile(
        file: Any,
        token: String,
        fileType: FileType,
        upKey: String,
        cancelTag: AtomicBoolean = AtomicBoolean(false),
        progressFn: UpProgressHandler = UpProgressHandler { _, _ -> },
    ): Deferred<Boolean> {
        val options = UploadOptions(emptyMap(), fileType.str, true, progressFn, cancelTag::get)
        val cd = CompletableDeferred<Boolean>()
        val completionFn = UpCompletionHandler { _, info, _ ->
            cd.complete(info.isOK)
        }
        when (file) {
            is Uri -> uploadManager.put(
                file, Local.ctx?.contentResolver, upKey, token, completionFn, options
            )

            is ByteArray -> uploadManager.put(file, upKey, token, completionFn, options)
            is String -> uploadManager.put(file, upKey, token, completionFn, options)
            is File -> uploadManager.put(file, upKey, token, completionFn, options)
            else -> CuLog.error(CuTag.Publish, "Bad file type")
        }
        return cd
    }

    fun genKey(
        type: FileType, ownerId: Long, number: Int = 0, x: Int = 0, y: Int = 0, d: Int = 0
    ): String {
        val body = "u_${ownerId}&t_${TimeUtils.getNow()}_${number}"
        return when (type) {
            FileType.Image -> "i_$body&x_${x}&y_${y}"
            FileType.Video -> "v_$body&x_${x}&y_${y}d_${d}"
            FileType.Audio -> "a_$body&d_${d}"
            FileType.Text -> "t_$body"
        }
    }

    fun uploadSingleFile(
        uri: Uri,
        ops: UPLOAD_OPS,
        fileType: FileType,
        completeFn: (String) -> Unit
    ) {
        MainCo.launch {
            LoginReq.uploadToken(UploadTokenDto(UPLOAD_OPS.toCode(ops))).await().map { token ->
                var key = ""
                if (fileType == FileType.Image) {
                    val size = ImageUtils.getParams(uri)
                    key = genKey(
                        type = FileType.Image,
                        ownerId = UserStore.userInfo.id,
                        number = 1,
                        x = size.width,
                        y = size.height,
                    )
                } else if (fileType == FileType.Video) {
                    val params = VideoUtils.getParams(uri)
                    key = genKey(
                        type = FileType.Video,
                        ownerId = UserStore.userInfo.id,
                        number = 1,
                        x = params.width,
                        y = params.height,
                        d = params.duration
                    )
                }

                uploadFile(
                    uri,
                    token = token,
                    fileType = fileType,
                    upKey = key,
                ).await()

                completeFn(key)
            }
        }
    }

}