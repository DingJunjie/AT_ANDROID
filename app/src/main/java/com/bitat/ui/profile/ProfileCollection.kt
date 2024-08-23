package com.bitat.ui.profile

import android.annotation.SuppressLint
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.bitat.R
import com.bitat.ext.timestampFormat
import com.bitat.repository.dto.resp.CollectPartDto
import com.bitat.router.NavigationItem
import com.bitat.state.CollectionTabs
import com.bitat.ui.component.MediaGrid
import com.bitat.utils.TimeUtils
import com.bitat.viewModel.CollectViewModel
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.Date


@Composable
fun CollectionTab(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {


    val vm: CollectViewModel = viewModelProvider[CollectViewModel::class]
    val state by vm.collectState.collectAsState()

    LaunchedEffect(Dispatchers.Default) {
        vm.initMyCollections()
        vm.getDefaultCollection()
    }

    Column(verticalArrangement = Arrangement.Top, modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.Top) {
            TextButton(onClick = { vm.setTab(CollectionTabs.Works) }) {
                Text("作品")
            }

            TextButton(onClick = { vm.setTab(CollectionTabs.Custom) }) {
                Text("自定义收藏夹")
            }

            TextButton(onClick = { vm.setTab(CollectionTabs.Music) }) {
                Text("音乐")
            }
        }

        when (state.currentTab) {
            CollectionTabs.Works -> MediaGrid(mediaList = state.currentCollectionItems)
            CollectionTabs.Custom -> CustomCollections(
                myCustomCollections = state.collections, {
                    vm.selectCollection(it)
                }, {
                    vm.createCollection(it)
                    vm.initMyCollections()
                }, navHostController
            )

            CollectionTabs.Music -> Box {}
        }

    }
}

@Composable
fun CustomCollections(
    myCustomCollections: List<CollectPartDto>,
    tapCollection: (CollectPartDto) -> Unit,
    createCollection: (String) -> Unit,
    navHostController: NavHostController
) {
    val isCreating = remember {
        mutableStateOf(false)
    }

    val bg by animateColorAsState(
        targetValue = if (isCreating.value) Color.Black else Color.LightGray,
        label = ""
    )

    val newCollectionName = remember {
        mutableStateOf("")
    }

    Column(verticalArrangement = Arrangement.Top) {
        Surface(
            color = bg,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .padding(horizontal = 20.dp)
        ) {
            if (isCreating.value) BasicTextField(
                value = newCollectionName.value,
                {
                    newCollectionName.value = it
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(35.dp)
                    .padding(top = 5.dp, bottom = 5.dp, start = 30.dp, end = 30.dp),
                textStyle = MaterialTheme.typography.body1.copy(color = Color.White),
                keyboardActions = KeyboardActions(onDone = { // 完成按键被点击时的回调
                    // 这里可以执行你的逻辑
                    createCollection(newCollectionName.value)
                    isCreating.value = false
                }),
                singleLine = true,
                cursorBrush = SolidColor(Color.White),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text, imeAction = ImeAction.Done
                ),
            ) { innerTextField ->
                Box(
                    Modifier
                        .border(1.dp, Color.Transparent, RoundedCornerShape(10.dp))
                        .padding(vertical = 2.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (newCollectionName.value.isEmpty()) Text(
                        "创建新的收藏夹",
                        color = Color.LightGray,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )

                    innerTextField()  // 显示实际的文本输入框
                }
            }
            else IconButton(onClick = { isCreating.value = true }) {
                Icon(Icons.Filled.Add, contentDescription = "")
            }
        }

        myCustomCollections.forEach {
            CollectionItem(it, tapCollection, navHostController)
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun CollectionItem(
    collection: CollectPartDto,
    tapCollection: (CollectPartDto) -> Unit,
    navHostController: NavHostController
) {
    Row(
        modifier = Modifier
            .padding(vertical = 5.dp, horizontal = 20.dp)
            .fillMaxWidth()
            .height(80.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(shape = RoundedCornerShape(20.dp), modifier = Modifier.padding(end = 10.dp)) {

            Box(modifier = Modifier
                .fillMaxWidth(0.5f)
                .fillMaxHeight()

//            .paint(
//                painter = rememberAsyncImagePainter(
//                    model = collection.cover
//                )
//            )
                .clickable {
                    tapCollection(collection)
                    navHostController.navigate(NavigationItem.CollectionDetail.route)
                }) {
                if (collection.cover.isEmpty()) {
                    Icon(Icons.Filled.AccountCircle, contentDescription = "")
                } else {
                    AsyncImage(
                        model = collection.cover,
                        contentDescription = "",
                        contentScale = ContentScale.Crop
                    )
                }
            }

        }

        Column {
            Text(collection.name)
            Text(collection.createTime.timestampFormat())
        }
    }
}