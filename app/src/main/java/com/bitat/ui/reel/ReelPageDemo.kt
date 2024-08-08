package com.bitat.ui.reel

import android.view.Surface
import androidx.annotation.IntDef
import androidx.compose.foundation.AndroidEmbeddedExternalSurface
import androidx.compose.foundation.AndroidExternalSurface
import androidx.compose.foundation.AndroidExternalSurfaceScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer

val videos = listOf(
    "https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/e25d81c4922fca5ebe51877717ef9b76.mp4",
    "https://www.w3schools.com/html/movie.mp4",
    "https://cdn.cnbj1.fds.api.mi-img.com/mi-mall/e25d81c4922fca5ebe51877717ef9b76.mp4",
)

@Composable
fun PlayerSurface(player: Player, surfaceType: @SurfaceType Int, modifier: Modifier = Modifier) {
    val onSurfaceCreated: (Surface) -> Unit = { surface -> player.setVideoSurface(surface) }
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
    val pagerState = rememberPagerState {
        videos.size
    }

    VerticalPager(state = pagerState) { index ->

        Box {
            val context = LocalContext.current
            val exoPlayer = remember {
                ExoPlayer.Builder(context).build().apply {
                    setMediaItem(MediaItem.fromUri(videos[index]))
                    prepare()
                    playWhenReady = false
                    repeatMode = Player.REPEAT_MODE_ONE
                }
            }
            PlayerSurface(
                player = exoPlayer,
                surfaceType = SURFACE_TYPE_SURFACE_VIEW, //                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
        }
    }
}