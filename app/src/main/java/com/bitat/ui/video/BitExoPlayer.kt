package com.bitat.ui.video

import android.net.Uri
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

/**
 *    author : shilu
 *    date   : 2024/8/12  14:50
 *    desc   :
 */

@Composable
fun BitExoPlay(uri: Uri) {

    val player = ExoPlayer.Builder(LocalContext.current).build()

    PlayerView(LocalContext.current)

    //添加播放列表
    //    val firstItem = MediaItem.fromUri(firstVideoUri)
    //    val secondItem = MediaItem.fromUri(secondVideoUri)
    //    // Add the media items to be played.
    //    player.addMediaItem(firstItem)
    //    player.addMediaItem(secondItem)

    // Build the media item.
    val mediaItem = MediaItem.fromUri(uri) // Set the media item to be played.
    player.setMediaItem(mediaItem) // Prepare the player.
    player.prepare() // Start the playback.
    player.play()
}


@Composable
fun VideoPlayer(modifier: Modifier=Modifier.fillMaxSize(),uri: String) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var exoPlayer: ExoPlayer? by remember { mutableStateOf(null) }

    DisposableEffect(lifecycleOwner) {
        val player = ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(uri)))
            prepare() //加载资源
            playWhenReady = true
            stop() //暂停播放
        }
        exoPlayer = player

        val lifecycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> player.playWhenReady = false
                Lifecycle.Event.ON_RESUME -> player.playWhenReady = true
                Lifecycle.Event.ON_DESTROY -> player.release()
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(lifecycleObserver)
            player.release()
        }
    }

    exoPlayer?.let { player ->
        Box(modifier = modifier) {
            AndroidView(factory = {
                PlayerView(context).apply {
                    this.player = player
                }
            }, modifier = Modifier.fillMaxSize())
        }
    }
}