package com.bitat.ui.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.bitat.ext.Density
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.state.DiscoveryState
import com.bitat.viewModel.DiscoveryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// 自定义卡片视图
@Composable
fun CustomCardView(
    color: Color = Color.Black,
    blog: BlogBaseDto,
    isTopCard: Boolean,
    isRemoved: Boolean
) {
    Box(
        modifier = Modifier
            .background(color, RoundedCornerShape(20.dp))
            .shadow(15.dp)
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        AsyncImage(
            model = blog.cover,
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(0.dp)
        )

        Column(
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomStart)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = blog.content,
                color = Color.White
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                AsyncImage(
                    model = blog.profile,
                    contentDescription = null,
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                        .border(1.dp, Color.White, CircleShape)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = blog.nickname,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.weight(1f))
                Button(onClick = {}) {
                    Text("关注")
                }
            }
        }

        LaunchedEffect(isTopCard) {
            if (isTopCard) {
                // 执行最上层卡片特定操作
                println("最上层卡片")
            }
        }

        LaunchedEffect(isRemoved) {
            if (isRemoved) {
                // 当卡片被移除时执行操作
                println("被移除了")
            }
        }
    }
}

// 卡片堆叠视图
@Composable
fun CardStack(
    data: List<BlogBaseDto>,
    visibleCardCount: Int = 4,
    onSwipe: (SwipeDirection) -> Unit,
    onLoadMore: () -> Unit,
    cardBuilder: @Composable (BlogBaseDto, Boolean, Boolean) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    var shownIndex by remember { mutableStateOf(0) }
    var removingTopCard by remember { mutableStateOf(false) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val density = LocalDensity.current.density

    val slice = remember {
        val sliceCount = if (removingTopCard) visibleCardCount + 1 else visibleCardCount
        val endIndex = minOf(data.size, shownIndex + sliceCount)
        data.subList(shownIndex, endIndex)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        slice.forEachIndexed { index, item ->
            Card(
//                item = item,
//                isTopCard = shownIndex == index,
//                isRemoved = removingTopCard && index == 0,
//                cardBuilder = cardBuilder,
                modifier = Modifier
                    .offset {
                        val xOffset = if (index == 0) offset.x.toDp() else 0.dp
                        val yOffset = if (index == 0) offset.y.toDp() else 0.dp
                        IntOffset(
                            xOffset
                                .toPx()
                                .toInt(),
                            yOffset
                                .toPx()
                                .toInt()
                        )
                    }
                    .graphicsLayer(
                        scaleX = 1f - (0.05f * index),
                        scaleY = 1f - (0.03f * index),
                        rotationZ = if (index == 0) (offset.x * 0.05f).coerceIn(-5f, 5f) else 0f
                    )
                    .zIndex(-index.toFloat())
                    .padding(16.dp)
                    .background(Color.Yellow)
            ) {

            }
        }

        Modifier.pointerInput(Unit) {
            detectTransformGestures { _, pan, zoom, _ ->
                if (pan != Offset.Zero) {
                    offset = pan
                }

                if (zoom > 1f) {
                    onSwipe(SwipeDirection.Right)
                }
            }
        }

        LaunchedEffect(shownIndex) {
            if (data.size - shownIndex <= visibleCardCount) {
                onLoadMore()
            }
        }
    }
}

// 滑动方向
enum class SwipeDirection {
    Left, Right, Up, Down
}

// 内容视图
@Composable
fun CardView() {
    val vm: DiscoveryViewModel = viewModel()
    val state by vm.discoveryState.collectAsState()
//    val waterFallState = remember { DiscoveryState() }

//    LaunchedEffect(Unit) {
//        waterFallState.discoveryList
//    }


    val coroutineScope = rememberCoroutineScope()
    LaunchedEffect(Dispatchers.Default) {
        vm.getDiscoveryList()
    }
    CardStack(
        data = state.discoveryList,
        onSwipe = { direction ->
            println("Swiped $direction")
        },
        onLoadMore = {
            coroutineScope.launch {

            }
        },
        cardBuilder = { item, isTopCard, isRemoved ->
            CustomCardView(
                blog = item,
                isTopCard = isTopCard,
                isRemoved = isRemoved
            )
        }
    )
}