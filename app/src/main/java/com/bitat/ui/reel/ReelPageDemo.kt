package com.bitat.ui.reel

import android.view.Surface
import androidx.annotation.IntDef
import androidx.compose.foundation.AndroidEmbeddedExternalSurface
import androidx.compose.foundation.AndroidExternalSurface
import androidx.compose.foundation.AndroidExternalSurfaceScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Vertices
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.itemsIndexed
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import com.bitat.ext.cdp
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.ui.video.BitExoPlay
import com.bitat.ui.video.VideoPlayer

val videos = listOf(
    "https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/e25d81c4922fca5ebe51877717ef9b76.mp4",
    "https://www.w3schools.com/html/movie.mp4",
    "https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/e25d81c4922fca5ebe51877717ef9b76.mp4",
    "https://www.w3schools.com/html/movie.mp4",
    "https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/e25d81c4922fca5ebe51877717ef9b76.mp4",
    "https://www.w3schools.com/html/movie.mp4",
    "https://www.w3schools.com/html/movie.mp4",
    "https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/e25d81c4922fca5ebe51877717ef9b76.mp4",
    "https://www.w3schools.com/html/movie.mp4",
    "https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/e25d81c4922fca5ebe51877717ef9b76.mp4",
    "https://www.w3schools.com/html/movie.mp4",
    "https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/e25d81c4922fca5ebe51877717ef9b76.mp4",
    "https://www.w3schools.com/html/movie.mp4",
    "https://www.w3schools.com/html/movie.mp4",
    "https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/e25d81c4922fca5ebe51877717ef9b76.mp4",
    "https://www.w3schools.com/html/movie.mp4",
    "https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/e25d81c4922fca5ebe51877717ef9b76.mp4",
    "https://www.w3schools.com/html/movie.mp4",
    "https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/e25d81c4922fca5ebe51877717ef9b76.mp4",
    "https://www.w3schools.com/html/movie.mp4",
    "https://www.w3schools.com/html/movie.mp4",
    "https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/e25d81c4922fca5ebe51877717ef9b76.mp4",
    "https://www.w3schools.com/html/movie.mp4",
)

@Composable
fun PlayerSurface(player: Player, surfaceType: @SurfaceType Int, modifier: Modifier = Modifier) {
    val onSurfaceCreated: (Surface) -> Unit = { surface ->
        player.setVideoSurface(surface)
    }
    val onSurfaceDestroyed: () -> Unit = { player.setVideoSurface(null) }
    val onSurfaceInitialized: AndroidExternalSurfaceScope.() -> Unit = {
        onSurface { surface, _, _ ->
            onSurfaceCreated(surface)
            surface.onDestroyed { onSurfaceDestroyed() }
        }
    }

    when (surfaceType) {
        SURFACE_TYPE_SURFACE_VIEW -> AndroidExternalSurface(modifier = modifier,
            onInit = onSurfaceInitialized)

        SURFACE_TYPE_TEXTURE_VIEW -> AndroidEmbeddedExternalSurface(modifier = modifier,
            onInit = onSurfaceInitialized)

        else -> throw IllegalArgumentException("Unrecognized surface type: $surfaceType")
    }
}


/**
 * The type of surface view used for media playbacks. One of [SURFACE_TYPE_SURFACE_VIEW] or
 * [SURFACE_TYPE_TEXTURE_VIEW].
 */
@MustBeDocumented
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPE, AnnotationTarget.TYPE_PARAMETER)
@IntDef(SURFACE_TYPE_SURFACE_VIEW, SURFACE_TYPE_TEXTURE_VIEW)
annotation class SurfaceType

/** Surface type equivalent to [SurfaceView] . */
const val SURFACE_TYPE_SURFACE_VIEW = 1

/** Surface type equivalent to [TextureView]. */
const val SURFACE_TYPE_TEXTURE_VIEW = 2


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReelPageDemo() {
    val pagerState = rememberPagerState(pageCount = { videos.size })

    //    VerticalPager(state = pagerState) { index ->
    //
    //        Box(modifier = Modifier.background(Color.Yellow)) { //            val context = LocalContext.current
    //            val exoPlayer = remember {
    //                ExoPlayer.Builder(context).build().apply {
    //                    setMediaItem(MediaItem.fromUri(videos[index]))
    //                    prepare()
    //                    playWhenReady = false
    //                    repeatMode = Player.REPEAT_MODE_ONE
    //                }
    //            }
    //            PlayerSurface(
    //                player = exoPlayer,
    //                surfaceType = SURFACE_TYPE_SURFACE_VIEW, //                modifier = Modifier.align(Alignment.CenterHorizontally),
    //            )
    //            val context = LocalContext.current
    //            val exoPlayer = remember {
    //                ExoPlayer.Builder(context).build().apply {
    //                    setMediaItem(MediaItem.fromUri(videos[index]))
    //                    prepare()
    //                    playWhenReady = false
    //                    repeatMode = Player.REPEAT_MODE_ONE
    //                }
    //            }
    //            PlayerSurface(
    //                player = exoPlayer,
    //                surfaceType = SURFACE_TYPE_SURFACE_VIEW, //                modifier = Modifier.align(Alignment.CenterHorizontally),
    //            )
    //            CuExoPlayer(data = index, modifier = Modifier.fillParentMaxSize(), true)
    //            BitExoPlay(uri = index)
    //            VideoPlayer(modifier = Modifier.matchParentSize(), uri = videos[index])

    //            CuExoPlayer(data = videos[index], modifier = Modifier.matchParentSize(), true)
    //    Text("页面测试$index")
    //
    //}

    //    ScalingLazyColumn() {
    //        items(items = (0..100)) { index ->
    //            Box {}
    //        }
    //    }


    //    for (i in 0..300) {
    //        videoArr.add("https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/e25d81c4922fca5ebe51877717ef9b76.mp4")
    //    }

    //    pageCount = { videoArr.size }, initialPage = 3
    //    val pagerState = rememberScalingLazyListState(2)


    //        ScalingLazyColumn(state = pagerState,
    //            modifier = Modifier.fillMaxSize().background(Color.Yellow)) {
    //            items(items = (videos)) { index ->
    //                Box(modifier = Modifier.fillParentMaxSize()
    //                    .background(Color.Cyan)) { //                val context = LocalContext.current
    //                    val exoPlayer = remember {
    //                        ExoPlayer.Builder(context).build().apply {
    //                            setMediaItem(MediaItem.fromUri(index))
    //                            prepare()
    //                            playWhenReady = false
    //                            repeatMode = Player.REPEAT_MODE_ONE
    //                        }
    //                    }
    //                    PlayerSurface(
    //                        player = exoPlayer,
    //                        surfaceType = SURFACE_TYPE_SURFACE_VIEW, //                modifier = Modifier.align(Alignment.CenterHorizontally),
    //                    )
    //                    CuExoPlayer(data = index, modifier = Modifier.fillParentMaxSize(), true)
    //                    BitExoPlay(uri = index)
    //                    VideoPlayer(modifier = Modifier.size(1920.cdp, 1080.cdp), uri = index)
    //                }
    //            }
    //    }

    //    val pagerState = rememberPagerState(pageCount = {
    //        10
    //    })
    //     { paddint ->

    // 监听当前页面的变化
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page -> // 页面切换时触发的操作
            CuLog.debug(CuTag.Blog, "Current page: $page")
        }
    }

    VerticalPager(state = pagerState) { page -> // Our page content
        //            Text(text = "Page: $page", modifier = Modifier.fillMaxWidth().height(100.dp))
        CuExoPlayer(data = videos[page], modifier = Modifier.fillMaxSize(), true)
    } //    }


}