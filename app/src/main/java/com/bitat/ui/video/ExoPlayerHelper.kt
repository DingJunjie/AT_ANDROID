package com.bitat.ui.video


import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.bitat.R

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun rememberPlayerView(exoPlayer: ExoPlayer, context: Context): PlayerView {
    val playerView = remember(context) {
        PlayerView(context).apply {

            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            useController = false
//            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH //设置为宽度撑满
            player = exoPlayer
            setBackgroundColor(context.resources.getColor( R.color.black,null))
            // 获取到PlayerView的Controller容器
            val controllerContainer = findViewById<FrameLayout>(R.id.exo_controller)
            // 动态替换自定义的Controller布局
            controllerContainer.removeAllViews()
//            LayoutInflater.from(context).inflate(R.layout.exo_playback_control_view, controllerContainer, true)
//            setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS)
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            playerView.player = null
        }
    }
    return playerView
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun rememberExoPlayerWithLifecycle(
    reelUrl: String,
    context: Context,
    isPause: Boolean
): ExoPlayer {
    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)

    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build()
            .apply {
                videoScalingMode = C.VIDEO_SCALING_MODE_SCALE_TO_FIT
                repeatMode = Player.REPEAT_MODE_ONE
                setHandleAudioBecomingNoisy(true)
            }
    }

    LaunchedEffect(reelUrl) {
        val videoUri = Uri.parse(reelUrl)
        val mediaItem = MediaItem.fromUri(videoUri)
        val dataSourceFactory = DefaultDataSource.Factory(context)
        val mediaSource =
            ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(mediaItem)
        exoPlayer.seekTo(0, 0)
        exoPlayer.setMediaSource(mediaSource)
        exoPlayer.prepare()
    }

    var appInBackground by remember {
        mutableStateOf(false)
    }

    DisposableEffect(key1 = lifecycleOwner, appInBackground) {
        val lifecycleObserver =
            getExoPlayerLifecycleObserver(exoPlayer, isPause, appInBackground) {
                appInBackground = it
            }
        lifecycleOwner.value.lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycleOwner.value.lifecycle.removeObserver(lifecycleObserver)
        }
    }
    return exoPlayer
}

