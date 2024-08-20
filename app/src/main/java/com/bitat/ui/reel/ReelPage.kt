package com.bitat.ui.reel

import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.bitat.R
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive


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
fun CuExoPlayer(
    data: String?,
    modifier: Modifier = Modifier,
    isFixHeight: Boolean = false,
    useExoController: Boolean = false,
    cache: Cache? = null,
    onSingleTap: (exoPlayer: ExoPlayer) -> Unit = {},
    onDoubleTap: (exoPlayer: ExoPlayer, offset: Offset) -> Unit = { _, _ -> },
    onVideoDispose: () -> Unit = {},
    onVideoGoBackground: () -> Unit = {}
) {
    val context = LocalContext.current //初始的比例，设置成这么大用来模拟 0 高度
    var ratio by remember { mutableStateOf(1000f) }

    //当前视频播放的进度
    var currentPosition by remember { mutableStateOf(0L) } //是否在播放
    var isVideoPlaying by remember { mutableStateOf(false) } //自己实现的控制器是否可见
    var isControllerVisible by remember { mutableStateOf(false) }

    var startRender by remember {
        mutableStateOf(false)
    }

    //标志是否为初次进入，防止 lifecycle 的 onStart 事件导致自动播放
    var isFirstIn by remember { mutableStateOf(true) }

    //实例化 ExoPlayer
    val exoPlayer = remember(context) {
        ExoPlayer.Builder(context).build().apply { //用视频网址构建 MediaItem
            val item = MediaItem.fromUri(Uri.parse(data))
            if (cache != null) { //启动缓存
                val httpDataSource =
                    DefaultHttpDataSource.Factory().setAllowCrossProtocolRedirects(true)
                val defaultDataSource = DefaultDataSource.Factory(context, httpDataSource)
                val cacheSourceFactory = CacheDataSource.Factory().setCache(cache)
                    .setUpstreamDataSourceFactory(defaultDataSource)
                    .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
                setMediaSource(
                    ProgressiveMediaSource.Factory(cacheSourceFactory)
                        .createMediaSource(item)
                )
            } else { //不启用缓存则直接 setMediaItem
                setMediaItem(item)
            } //设置重复播放的模式（这里也不是很搞得懂）
            repeatMode = Player.REPEAT_MODE_ONE //自动重播
            playWhenReady = false //开始准备资源

            prepare()
            this.seekTo(0)
        }
    }


    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)
    DisposableEffect(lifecycleOwner) {
        val lifeCycleObserver = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> { //暂停视频播放并调用 onVideoGoBackground
                    CuLog.info(CuTag.Blog, "VideoPlayer------------->>>> ON_STOP$data")
                    exoPlayer.pause()
                    onVideoGoBackground()
                }

                Lifecycle.Event.ON_START -> {
                    CuLog.info(CuTag.Blog, "VideoPlayer------------->>>> ON_START$data")
                    if (!isFirstIn) exoPlayer.play()
                } //恢复播放

                Lifecycle.Event.ON_PAUSE -> {
                    CuLog.info(CuTag.Blog, "VideoPlayer------------->>>> ON_PAUSE$data")

                }

                Lifecycle.Event.ON_RESUME -> {
                    CuLog.info(CuTag.Blog, "VideoPlayer------------->>>> ON_PAUSE$data")
                }

                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(lifeCycleObserver)
        onDispose {
            CuLog.info(CuTag.Blog, "VideoPlayer------------->>>> onDispose$data")
            exoPlayer.stop()
            exoPlayer.release()
            lifecycleOwner.lifecycle.removeObserver(lifeCycleObserver)
        }
    }

    LaunchedEffect(isControllerVisible) {
        if (isControllerVisible) { //如果控制器可见，5 秒后自动消失
            delay(5000)
            isControllerVisible = false
        }
        exoPlayer.play()//控制自动播放
        CuLog.info(CuTag.Blog, "VideoPlayer------------->>>> LaunchedEffect")
    }

    //构建播放器控件
    //    val playerView = remember {
    //        //这里使用了 XML 的布局构建 view 是因为项目需要设置播放器的渲染方式，只能用 XML 的属性设置
    //        LayoutInflater.from(context).inflate(R.layout.video_player, null)
    //            .findViewById<PlayerView>(R.id.my_video_player).apply {
    //                player = exoPlayer
    //                useController = useExoController
    //            }
    //    }


    DisposableEffect(Unit) { //                playerView.setAspectRatioListener { targetAspectRatio, _, _ ->
        //                    //获取到视频比例时给控件比例赋值
        //                    ratio = targetAspectRatio
        //                }
        val listener = object : Player.Listener {
            override fun onIsPlayingChanged(isPlaying: Boolean) { //是否正在播放的监听
                isVideoPlaying = isPlaying
            }

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (playbackState == Player.STATE_READY) {
                    startRender = true
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose { //收尾工作
//            CuLog.info(CuTag.Blog, "VideoPlayer------------->>>> onDispose")
//            exoPlayer.release()
//            onVideoDispose()
//            exoPlayer.removeListener(listener) //            playerView.setAspectRatioListener(null)

        }
    }

    LaunchedEffect(exoPlayer) {
        while (isActive) { //每 1 秒读一次当前进度，用于自定义控制器的进度显示
            currentPosition = exoPlayer.currentPosition / 1000
            delay(1000)
        }
    }

    val singleTapWrapper: (Offset) -> Unit = { //单击回调装饰器，控制自定义控制器的可见性并回调 onSingleTap
        isControllerVisible = !isControllerVisible
        onSingleTap(exoPlayer)
    }

    val doubleTapWrapper: (Offset) -> Unit = { //双击回调装饰器，控制视频的暂停和播放并回调 onDoubleTap
        if (exoPlayer.isPlaying) exoPlayer.pause()
        else exoPlayer.play()
        isFirstIn = false
        onDoubleTap(exoPlayer, it)
    }

    val actualModifier = if (isFixHeight) {
        modifier
    } else { //非指定高度则设置控件比例
        modifier.aspectRatio(ratio)
    }

    Box(modifier = actualModifier) { //播放器本体
        //        AndroidView(
        //            factory = { playerView },
        //            modifier = actualModifier.pointerInput(Unit) {
        //                detectTapGestures(
        //                    onTap = singleTapWrapper,
        //                    onDoubleTap = doubleTapWrapper
        //                )
        //            }
        //        )
        //        PlayerSurface(exoPlayer, surfaceType = SURFACE_TYPE_SURFACE_VIEW)

        Box(modifier = modifier) {
            if (startRender) AndroidView(factory = {
                PlayerView(context).apply {
                    this.player = exoPlayer
//                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT // 设置视频内容按原分辨率显示，不进行拉升
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH //设置为宽度撑满
                    useController = false // 不显示默认控制器
                    setBackgroundColor(context.resources.getColor(R.color.black))

                }
            }, modifier = Modifier.fillMaxSize())
        }


        //以下是自定义控制器的 UI，使用 Exoplayer 内置控制器时不显示
        if (!useExoController) {

            val controllerBgAlpha by animateFloatAsState(targetValue = if (isControllerVisible || isFirstIn) 0.7f else 0f)
            val controllerContentAlpha by animateFloatAsState(targetValue = if (isControllerVisible || isFirstIn) 1f else 0f)

            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
                    .height(20.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = controllerBgAlpha))
                    .padding(horizontal = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                val formattedTime =
                    "${currentPosition / 60}:${String.format("%02d", (currentPosition % 60))}"
                Text(
                    text = formattedTime,
                    color = Color.White.copy(alpha = controllerContentAlpha),
                    style = MaterialTheme.typography.labelSmall
                )
            }

            Box(modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(10.dp)
                .size(30.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = controllerBgAlpha))
                .clickable {
                    if (exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
                    isFirstIn = false
                }
                .padding(6.dp), contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (isVideoPlaying) Icons.Filled.Phone else Icons.Filled.PlayArrow,
                    contentDescription = "",
                    tint = Color.White.copy(alpha = controllerContentAlpha)
                )
            }

        }
    }
}


