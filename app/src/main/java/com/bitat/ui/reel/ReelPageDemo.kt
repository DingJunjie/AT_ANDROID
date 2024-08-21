package com.bitat.ui.reel

import android.annotation.SuppressLint
import android.view.Surface
import androidx.annotation.IntDef
import androidx.compose.foundation.AndroidEmbeddedExternalSurface
import androidx.compose.foundation.AndroidExternalSurface
import androidx.compose.foundation.AndroidExternalSurfaceScope
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.Player
import androidx.navigation.NavHostController
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.BLOG_IMAGES_ONLY
import com.bitat.repository.consts.BLOG_IMAGE_TEXT
import com.bitat.repository.consts.BLOG_VIDEO_ONLY
import com.bitat.repository.consts.BLOG_VIDEO_TEXT
import com.bitat.ui.component.ImageBanner
import com.bitat.viewModel.BlogViewModel

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
        SURFACE_TYPE_SURFACE_VIEW -> AndroidExternalSurface(
            modifier = modifier,
            onInit = onSurfaceInitialized
        )

        SURFACE_TYPE_TEXTURE_VIEW -> AndroidEmbeddedExternalSurface(
            modifier = modifier,
            onInit = onSurfaceInitialized
        )

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


@SuppressLint("UnrememberedMutableState")
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ReelPageDemo(navController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm = viewModelProvider[BlogViewModel::class]
    val state = vm.blogState.collectAsState()
    val pagerState =
        rememberPagerState(
            initialPage = state.value.resIndex,
            pageCount = { state.value.resList.size })

    // 监听当前页面的变化
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page -> // 页面切换时触发的操作
            CuLog.debug(CuTag.Blog, "1111Current page: $page")
            vm.setResIndex(page)
        }
    }

    val isPlay = mutableStateOf(true)

    VerticalPager(state = pagerState) { page -> // Our page content
        //            Text(text = "Page: $page", modifier = Modifier.fillMaxWidth().height(100.dp))
        val currentDto = state.value.resList[page]
        CuLog.debug(CuTag.Blog, "22222页面加载/重组: $page")

        when (currentDto.kind.toInt()) {
            BLOG_VIDEO_ONLY, BLOG_VIDEO_TEXT -> {
                if (page == state.value.resIndex) {
//                    isPlay.val ue = page == state.value.resIndex
                    CuExoPlayer(
                        data = currentDto.resource.video,
                        modifier = Modifier.fillMaxSize(),
                        cover = currentDto.cover,
                        isFixHeight = true
                    )
                }

                CuLog.debug(
                    CuTag.Blog,
                    "333页面加载/重组完成: index：$page，${page == state.value.resIndex}，isPlay： ${isPlay.value}"
                )
            }

            BLOG_IMAGE_TEXT, BLOG_IMAGES_ONLY -> {
                ImageBanner(currentDto.resource.images.toList())
            }
        }

    }
}