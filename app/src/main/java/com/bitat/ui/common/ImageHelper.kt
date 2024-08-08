package com.bitat.ui.common

import kotlin.math.abs
import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.bitat.utils.ScreenUtils


/*****
 * 显示svg的图片
 */
@Composable
fun rememberSvgPainter(
    model: Any?,
    imageLoader: ImageLoader = rememberImageLoader(),
    transform: (AsyncImagePainter.State) -> AsyncImagePainter.State = AsyncImagePainter.DefaultTransform,
    onState: ((AsyncImagePainter.State) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
): AsyncImagePainter {
    return rememberAsyncImagePainter(
        model = model,
        imageLoader = imageLoader,
        transform = transform,
        onState = onState,
        contentScale = contentScale,
        filterQuality = filterQuality
    )
}

@Composable
fun rememberAssetsPainter(
    path: String,
    imageLoader: ImageLoader = rememberImageLoader(),
    transform: (AsyncImagePainter.State) -> AsyncImagePainter.State = AsyncImagePainter.DefaultTransform,
    onState: ((AsyncImagePainter.State) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
): AsyncImagePainter {
    return rememberAsyncImagePainter(
        model = "file:///android_asset/${path}",
        imageLoader = imageLoader,
        transform = transform,
        onState = onState,
        contentScale = contentScale,
        filterQuality = filterQuality
    )
}

@Composable
fun rememberAsyncAssetPainter(
    path: String,
    imageLoader: ImageLoader = rememberImageLoader(),
    transform: (AsyncImagePainter.State) -> AsyncImagePainter.State = AsyncImagePainter.DefaultTransform,
    onState: ((AsyncImagePainter.State) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
): AsyncImagePainter {
    return rememberAsyncImagePainter(
        model = "file:///android_asset/${path}",
        imageLoader = imageLoader,
        transform = transform,
        onState = onState,
        contentScale = contentScale,
        filterQuality = filterQuality
    )
}

@Composable
fun rememberAsyncResPainter(
    path: String,
    imageLoader: ImageLoader = rememberImageLoader(),
    transform: (AsyncImagePainter.State) -> AsyncImagePainter.State = AsyncImagePainter.DefaultTransform,
    onState: ((AsyncImagePainter.State) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
): AsyncImagePainter {
    return rememberAsyncImagePainter(
        model = "file:///android_/${path}",
        imageLoader = imageLoader,
        transform = transform,
        onState = onState,
        contentScale = contentScale,
        filterQuality = filterQuality
    )
}

@Composable
fun rememberAsyncPainter(
    url: String,
    imageLoader: ImageLoader = rememberImageLoader(),
    transform: (AsyncImagePainter.State) -> AsyncImagePainter.State = AsyncImagePainter.DefaultTransform,
    onState: ((AsyncImagePainter.State) -> Unit)? = null,
    contentScale: ContentScale = ContentScale.Fit,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
): AsyncImagePainter {
    return rememberAsyncImagePainter(
        model = url,
        imageLoader = imageLoader,
        transform = transform,
        onState = onState,
        contentScale = contentScale,
        filterQuality = filterQuality
    )
}

@Composable
fun SvgIcon(
    path: String,
    contentDescription: String?,
    @SuppressLint("ModifierParameter")
    modifier: Modifier = Modifier
        .width(30.dp)
        .height(30.dp),
    tint: Color = LocalContentColor.current,
) {
    val imageLoader: ImageLoader = ImageLoader.Builder(LocalContext.current).components {
        add(SvgDecoder.Factory())
    }.build()

    Icon(
        painter = rememberAssetsPainter(path = path, imageLoader = imageLoader),
        contentDescription,
        modifier,
        tint
    )
}

@Composable
fun SvgIcon(
    painter: Painter,
    contentDescription: String?,
    @SuppressLint("ModifierParameter")
    modifier: Modifier = Modifier.size(24.dp),
    tint: Color = LocalContentColor.current
) {
    Icon(painter = painter, contentDescription, modifier, tint)
}

@Composable
private fun rememberImageLoader(context: Context = LocalContext.current): ImageLoader {
    val loader = remember {
        ImageLoader.Builder(context)
            .components {
                add(SvgDecoder.Factory())
            }
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .build()
    }
    return loader

}

