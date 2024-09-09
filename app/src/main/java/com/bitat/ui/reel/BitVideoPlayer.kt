package com.bitat.ui.reel

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.annotation.OptIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.Cache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
//import chaintech.videoplayer.ui.video.VideoPlayerView
import com.bitat.R
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.video.CMPPlayer
import com.bitat.ui.video.PlayerConfig
import com.bitat.ui.video.PlayerSpeed
import kotlinx.coroutines.delay


/**
 * Compose 封装的 ExoPlayer
 * @param data String? 视频的网络地址
 * @param modifier Modifier
 * @param isFixHeight Boolean 用于指定控件是否固定高度，true 则需传入与高度相关的 Modifier；false 则根据视频比例调整
 * @param useExoController Boolean 是否使用 ExoPlayer 的内置控制器
 * @param cache Cache? 视频缓存功能，null 表明不启用缓存
 * @param onSingleTap 单击视频控件事件回调
 * @param onDoubleTap 双击视频控件事件回调
 * @param onVideoDispose ExoPlayer release 后的回调
 * @param onVideoGoBackground 后台事件回调
 */
@OptIn(UnstableApi::class)
@Composable
fun BitVideoPlayer(
    data: String,
    modifier: Modifier = Modifier,
    isFixHeight: Boolean = false,
    cover: String,
    soundShow: Boolean = false,
    cache: Cache? = null,
    onSingleTap: () -> Unit = {},
    onDoubleTap: (exoPlayer: ExoPlayer, offset: Offset) -> Unit = { _, _ -> },
    onVideoDispose: () -> Unit = {},
    onVideoGoBackground: () -> Unit = {}
) {
    val context = LocalContext.current //初始的比例，设置成这么大用来模拟 0 高度

//    val playerView = remember(context) {
//        PlayerView(context).apply {
//
//            layoutParams = ViewGroup.LayoutParams(
//                ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT
//            )
//
//            useController = false
////            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
//            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH //设置为宽度撑满
//            player = exoPlayer
//            // 获取到PlayerView的Controller容器
//            val controllerContainer = findViewById<FrameLayout>(R.id.exo_controller)
//            // 动态替换自定义的Controller布局
//            controllerContainer.removeAllViews()
////            LayoutInflater.from(context).inflate(R.layout.exo_playback_control_view, controllerContainer, true)
////            setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
//        }
//    }

    var ratio by remember { mutableStateOf(1000f) }

    //当前视频播放的进度
    var currentPosition by remember { mutableStateOf(0L) } //是否在播放
    var currentPositionMs by remember { mutableStateOf(0L) }
    var isVideoPlaying by remember { mutableStateOf(false) } //自己实现的控制器是否可见
    var isControllerVisible by remember { mutableStateOf(false) }

    var renderImage by remember {
        mutableStateOf(true)
    }

    var isShow by remember {
        mutableStateOf(true)
    }

    var config by  remember {
        mutableStateOf(PlayerConfig())
    }

    //标志是否为初次进入，防止 lifecycle 的 onStart 事件导致自动播放
    var isFirstIn by remember { mutableStateOf(true) }

    val singleTapWrapper: (Offset) -> Unit = { //单击回调装饰器，控制自定义控制器的可见性并回调 onSingleTap
        isControllerVisible = !isControllerVisible
        onSingleTap()
    }


    val actualModifier = if (isFixHeight) {
        modifier
    } else { //非指定高度则设置控件比例
        modifier.aspectRatio(ratio)
    }


    Box(modifier = modifier) { //播放器本体
        if (isShow) {
            Box(modifier = modifier) {
                CMPPlayer(
                    modifier = Modifier.fillMaxSize(),
                    url = data,
                    isPause = false,
                    isMute = VideoConfig.isPlayVolume,
                    totalTime = {},
                    currentTime = {},
                    isSliding = false,
                    sliderTime = null,
                    config=config,
                    speed = PlayerSpeed.X1){ exoPlayer ->
                }
            }
        }


        // }

        Box(modifier = Modifier
            .fillMaxSize()
            .clickable {
                CuLog.error(CuTag.Blog, "点击了视频区域")
                onSingleTap()
            }) {

            if (soundShow)
                Box(modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .size(30.dp)
//            .clip(CircleShape).background(Color.Black)
                    .clickable {

                isFirstIn = false
                        VideoConfig.isPlayVolume = !VideoConfig.isPlayVolume
                        config.isMute=   VideoConfig.isPlayVolume
                    }
                    .padding(6.dp),
                    contentAlignment = Alignment.Center) {
                    //            Icon(if (isVideoPlaying) Icons.Filled.Phone else Icons.Filled.PlayArrow,
                    //                contentDescription = "",
                    //                tint = Color.White)
                    //            )
                    //            video-sound.svg
                    SvgIcon(
                        path = "svg/video-sound.svg",
                        tint = Color.White,
                        contentDescription = ""
                    )
                }
        }
    }
}




