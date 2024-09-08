package com.bitat.ui.video


import android.graphics.PorterDuff
import android.widget.ProgressBar
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.bitat.R
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun CMPPlayer(
    modifier: Modifier,
    url: String,
    isPause: Boolean,
    isMute: Boolean,
    totalTime: ((Int) -> Unit),
    currentTime: ((Int) -> Unit),
    isSliding: Boolean,
    sliderTime: Int?,
    speed: PlayerSpeed,config: PlayerConfig=PlayerConfig(),
    videoReady: (ExoPlayer) -> Unit = {}
) {
    val context = LocalContext.current
    val exoPlayer = rememberExoPlayerWithLifecycle(url, context, isPause)
    val playerView = rememberPlayerView(exoPlayer, context)
    val progressBarColor by remember { mutableStateOf(Color.White) }  // 默认颜色

    LaunchedEffect(exoPlayer) {
        while (isActive) {
            currentTime(exoPlayer.currentPosition.coerceAtLeast(0L).toInt())
            delay(1000) // Delay for 1 second
        }
    }

    LaunchedEffect(playerView) {
        playerView.keepScreenOn = true
    }

    Box {
        AndroidView(
            factory = { playerView },
            modifier = modifier,
            update = { view ->
                setProgressBarColor(view, progressBarColor)
                exoPlayer.playWhenReady = !isPause
                exoPlayer.volume = if (isMute) {
                    0f
                } else {
                    1f
                }
                sliderTime?.let {
                    exoPlayer.seekTo(it.toLong())
                }
                exoPlayer.setPlaybackSpeed(
                    when (speed) {
                        PlayerSpeed.X0_5 -> 0.5f
                        PlayerSpeed.X1 -> 1f
                        PlayerSpeed.X1_5 -> 1.5f
                        PlayerSpeed.X2 -> 2f
                    }
                )
            }
        )

        DisposableEffect(key1 = Unit) {
            val listener = object : Player.Listener {
                override fun onEvents(
                    player: Player, events: Player.Events
                ) {
                    super.onEvents(player, events)
                    if (!isSliding) {
                        totalTime(player.duration.coerceAtLeast(0L).toInt())
                        currentTime(player.currentPosition.coerceAtLeast(0L).toInt())
                    }
                }

                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    if (isPlaying) {
                        // 播放开始
                        println("Player is playing")
                    } else {
                        // 播放暂停
                        println("Player is paused")
                    }
                }

                override fun onPlaybackStateChanged(playbackState: Int) {
                    when (playbackState) {
                        Player.STATE_IDLE -> {
                            // 播放器为空闲状态
                            CuLog.debug(CuTag.Blog, "CMPVideo 空闲")
                        }

                        Player.STATE_BUFFERING -> {
                            // 播放器正在缓冲
                            CuLog.debug(CuTag.Blog, "CMPVideo 加载中")
                            videoReady(exoPlayer)
                        }

                        Player.STATE_READY -> {
                            // 播放器已经准备好，且能够立即播放
                            CuLog.debug(CuTag.Blog, "CMPVideo 准备完毕")
                        }

                        Player.STATE_ENDED -> {
                            // 播放结束
                            CuLog.debug(CuTag.Blog, "CMPVideo 播放结束")
                        }
                    }
                }

                override fun onPlayerError(error: PlaybackException) {
                    super.onPlayerError(error)
                }
            }

            exoPlayer.addListener(listener)

            onDispose {
                exoPlayer.removeListener(listener)
                exoPlayer.release()
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
            playerView.keepScreenOn = false
        }
    }

    fun setMute(isMute: Boolean) {
        exoPlayer.volume = if (isMute) 0f else 1f

    }
}

private fun setProgressBarColor(playerView: PlayerView, color: Color) {
    val progressBar = playerView.findViewById<ProgressBar>(R.id.exo_loading)
    progressBar?.indeterminateDrawable?.setColorFilter(color.toArgb(), PorterDuff.Mode.SRC_IN)
}