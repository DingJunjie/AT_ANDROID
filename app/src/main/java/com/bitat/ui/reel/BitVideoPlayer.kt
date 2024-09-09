package com.bitat.ui.reel

import androidx.annotation.OptIn
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import coil.compose.AsyncImage
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.video.CMPPlayer
import com.bitat.ui.video.PlayerConfig
import com.bitat.ui.video.PlayerSpeed


enum class BitVideoType { VideoList, VideoReel }

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
    data: String, modifier: Modifier = Modifier, isFixHeight: Boolean = false, cover: String, soundShow: Boolean = false,
    config: PlayerConfig = PlayerConfig(),
    type: BitVideoType,
    onSingleTap: () -> Unit = {}, onDoubleTap: (exoPlayer: ExoPlayer, offset: Offset) -> Unit = { _, _ -> },
    onVideoDispose: () -> Unit = {}, onVideoGoBackground: () -> Unit = {},
) {

    var ratio by remember { mutableStateOf(1000f) }

    var isControllerVisible by remember { mutableStateOf(false) }

    var renderImage by remember {
        mutableStateOf(true)
    }

    val isShow by remember {
        mutableStateOf(true)
    }


    //标志是否为初次进入，防止 lifecycle 的 onStart 事件导致自动播放
    var isFirstIn by remember { mutableStateOf(true) }
    val actualModifier = if (isFixHeight) {
        modifier
    } else { //非指定高度则设置控件比例
        modifier.aspectRatio(ratio)
    }
    Box(modifier = modifier) { //播放器本体
        if (isShow) {
            Box(modifier = modifier) {
                CMPPlayer(modifier = Modifier.fillMaxSize(),
                    url = data,
                    isPause = false,
                    totalTime = {},
                    currentTime = {},
                    isSliding = false,
                    sliderTime = null,
                    config = config,
                    speed = PlayerSpeed.X1) {
                    renderImage = false
                }
            }
            if (renderImage) Box(modifier = modifier) {
                AsyncImage(model = cover,
                    modifier = Modifier //                        .clip(RoundedCornerShape(8.dp))
                        .fillMaxWidth()
                        .align(Alignment.Center) //                        .fillMaxHeight()
                        .background(Color.Transparent),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth)
            }
        }

        Box(modifier = Modifier.fillMaxSize().clickable(onClick = {
            when (type) {
                BitVideoType.VideoList -> onSingleTap()
                BitVideoType.VideoReel -> {
                    config.isPause.value = !config.isPause.value
                    onSingleTap()
                }
            } },
            indication = null, interactionSource = remember { MutableInteractionSource() }
            )) {
            if (soundShow) Box(modifier = Modifier.align(Alignment.BottomEnd).padding(10.dp)
                .size(30.dp).clickable(onClick = {
                    isFirstIn = false
                    VideoConfig.isPlayVolume = !VideoConfig.isPlayVolume
                    config.isMute.value = VideoConfig.isPlayVolume
                }, indication = null, interactionSource = remember { MutableInteractionSource() })
                .padding(6.dp),
                contentAlignment = Alignment.Center) { //            Icon(if (isVideoPlaying) Icons.Filled.Phone else Icons.Filled.PlayArrow,
                //                contentDescription = "",
                //                tint = Color.White)
                //            )
                //            video-sound.svg
                SvgIcon(path = "svg/video-sound.svg", tint = Color.White, contentDescription = "")
            }
        }
    }
}




