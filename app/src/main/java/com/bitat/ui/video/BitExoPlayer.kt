package com.bitat.ui.video

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
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