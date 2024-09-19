package com.bitat.utils

import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.ReturnCode
import com.bitat.MainCo
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.store.FileStore
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.io.File

object FFMpegUtil {

//    fun getCover(path: String, frame: Int = 0, quality: Int = 3, completeFn: () -> Unit = {}) {
//        if (path == "") return
//        val file = File(path)
//
//        if (!file.exists()) {
//            return
//        }
//
//        val outputPath = FileStore.cacheImageFolder + "/" + TimeUtils.getNow().toString() + ".jpg"
//        println(outputPath)
//
//        val cmd = "-i $path -vf 'select=eq(n\\,$frame)' -q:v $quality $outputPath";
//
//        MainCo.launch(IO) {
//            val session: FFmpegSession = FFmpegKit.execute(cmd)
//
//            if (ReturnCode.isSuccess(session.returnCode)) {
//                println("ffmpeg did well")
//            } else if (ReturnCode.isCancel(session.returnCode)) {
//                println("ffmpeg cancel")
//            } else {
//                CuLog.info(
//                    CuTag.Publish,
//                    String.format(
//                        "Command failed with state %s and rc %s.%s",
//                        session.state,
//                        session.returnCode,
//                        session.failStackTrace
//                    )
//                );
//
//            }
//        }
//    }
}

//    Future<String?> getFirstFrameOfVideo(String videoPath,
//       {int frame = 0,
//       int qulity = 3,
//       Function? completeFunc,
//       Function? errorFunc}) async {
//     String cmd = "";

//     File videoFile = File(videoPath);
//     if (!videoFile.existsSync()) {
//       print("视频不存在");
//       return null;
//     }

//     final outputFrame = "${LocalImageUtil.cacheFolderPath}/tmp_frame.jpg";

//     File frameFile = File(outputFrame);
//     if (frameFile.existsSync()) {
//       await frameFile.delete();
//     }

//     cmd = "-i $videoPath -vf 'select=eq(n\\,$frame)' -q:v $qulity $outputFrame";

//     final session = await FFmpegKit.execute(cmd);

//     final returnCode = await session.getReturnCode();

//     if (ReturnCode.isSuccess(returnCode)) {
//       // SUCCESS
//       print("setSuccess");
//       // completeFunc?.call(outputFrame);
//       return outputFrame;
//     } else if (ReturnCode.isCancel(returnCode)) {
//       // CANCEL
//       print("setCancel");
//     } else {
//       // ERROR
//       print("setError");
//       errorFunc?.call();
//     }
//     return null;
//   }
//}