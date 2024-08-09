package com.bitat.ui.publish

import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bitat.R
import com.bitat.ext.cdp
import com.bitat.ext.clickableWithoutRipple
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.Followable
import com.bitat.repository.consts.Visibility
import com.bitat.repository.dto.resp.BlogTagDto
import com.bitat.repository.dto.resp.UserDto
import com.bitat.router.AtNavigation
import com.bitat.ui.blog.Avatar
import com.bitat.ui.common.CarmeraOpen
import com.bitat.ui.common.GDMapPage
import com.bitat.ui.common.ImagePicker
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.component.BackButton
import com.bitat.utils.GaoDeUtils
import com.bitat.utils.ScreenUtils
import com.bitat.viewModel.PublishViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.melody.dialog.any_pop.AnyPopDialog
import com.melody.dialog.any_pop.AnyPopDialogProperties
import com.melody.dialog.any_pop.DirectionState

enum class PublishTextOption {
    Topic, At, Follow, Font, Visibility, None, Media, Location
}

@OptIn(ExperimentalMaterial3Api::class,
    ExperimentalMaterialApi::class,
    ExperimentalPermissionsApi::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun PublishDetailPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm = viewModelProvider[PublishViewModel::class]
    val commonState by vm.commonState.collectAsState()
    val mediaState by vm.mediaState.collectAsState()
    val showDialog by remember { mutableStateOf(false) }
    val dialogResult by rememberSaveable { mutableStateOf("") }


    var option by remember {
        mutableStateOf(PublishTextOption.None)
    }

    val permissionState =
        rememberMultiplePermissionsState(permissions = listOf(android.Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))

    val selectedUri = remember {
        mutableStateOf(Uri.EMPTY)
    }

    vm.initTags()

    var showTopicDialog by remember { mutableStateOf(false) }
    OptionDialog(showTopicDialog,
        onDismiss = { showTopicDialog = false; option = PublishTextOption.None }) {
        when (option) {
            PublishTextOption.Follow -> FollowOptions(currentFollowable = Followable.getFollowable(
                commonState.followId), setFollowFn = {
                vm.onFollowClick(it)
                option = PublishTextOption.None
                showTopicDialog = false
            })

            PublishTextOption.Visibility -> VisibilityOptions(currentVisibility = commonState.visibility,
                setVisibilityFn = {
                    vm.onVisibilityClick(it)
                    option = PublishTextOption.None
                    showTopicDialog = false
                })

            PublishTextOption.Topic -> {
                TopicOptions(tags = commonState.tagSearchResult, tapTopicFn = {
                    vm.onTopicClick(it)
                    option = PublishTextOption.None
                    showTopicDialog = false
                })
            }

            PublishTextOption.Media -> {
                MediaOptions(selectedUri.value, editFn = {

                }, removeFn = {
                    vm.removeMedia(it)
                    option = PublishTextOption.None
                    showTopicDialog = false
                })
            }

            PublishTextOption.At -> { //                TopicOptions(tags = commonState.atUserSearchResult, tapTopicFn = {
                //                    vm.onTopicClick(it)
                //                    option = PublishTextOption.None
                //                    showTopicDialog = false
                //                })
            }

            else -> Text("hahaha")

        }
    }



    fun tapOption(opt: PublishTextOption) {
        if (option == opt) {
            option = PublishTextOption.None
            showTopicDialog = false
        } else {
            option = opt
            showTopicDialog = true
        }
    }



    Scaffold(topBar = {
        TopBar(backTapFn = {
            navHostController.popBackStack()
        })
    }, modifier = Modifier.fillMaxSize()) { padding ->
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
            if (permissionState.allPermissionsGranted) {

                Column(modifier = Modifier.padding(padding) //                    .fillMaxHeight()
                ) {

                    if (mediaState.localImages.isNotEmpty() || mediaState.localCover != Uri.EMPTY) MediaBox(
                        mediaState.localImages,
                        selectUri = {
                            selectedUri.value = it
                            option = PublishTextOption.Media
                            showTopicDialog = true
                        },
                        addPicture = {
                            vm.addPicture(it)
                        },
                        coverPath = mediaState.localCover)
                    InputBox(hasMedia = mediaState.localImages.isNotEmpty(),
                        content = commonState.content,
                        addPicture = { vm.addPicture(it) },
                        updateContent = { vm.onContentChange(it) }) //                    LazyRow(modifier = Modifier.padding(10.dp),
                    //                        horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    //                        items(commonState.tags) { item ->
                    //                            Box(modifier = Modifier.border(1.dp,
                    //                                Color.Black,
                    //                                RoundedCornerShape(20.dp)).padding(all = 10.dp)) {
                    //                                Text(text = "#${item.name}")
                    //                                IconButton(onClick = {}) { //                                    Icon(painterResource(R.drawable.))
                    //
                    //
                    //                                }
                    //                            }
                    //                        }
                    //                    }
                    Row(horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.padding(start = 20.dp)) {
                        Options(title = stringResource(id = R.string.publish_option_topic),
                            iconPath = "svg/topic.svg",
                            selected = option == PublishTextOption.Topic,
                            tapFn = {
                                tapOption(PublishTextOption.Topic)
                            })

                        Options(title = stringResource(id = R.string.publish_option_at),
                            iconPath = "svg/at.svg",
                            selected = option == PublishTextOption.At,
                            modifier = Modifier.size(18.dp),
                            tapFn = {
                                tapOption(PublishTextOption.At)
                            })

                        Options(title = if (commonState.location == "") stringResource(id = R.string.publish_location_add) else commonState.location,
                            iconPath = "svg/location_line.svg",
                            selected = option == PublishTextOption.Location,
                            modifier = Modifier.size(18.dp),
                            tapFn = { //                                tapOption(PublishTextOption.Location)

                                GaoDeUtils.getLocation() { point, name ->
                                    vm.locationUpdate(point, name)

                                } //                                showTopicDialog = false;
                                //                                option = PublishTextOption.None

                            })
                    }

                    HorizontalDivider(color = Color(0xffeeeeee),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 30.dp))

                    //                Spacer(modifier = Modifier.height(2.dp))

                    Follow() {
                        tapOption(PublishTextOption.Follow)
                    }

                    VisibilityRow(commonState.visibility) {
                        tapOption(PublishTextOption.Visibility)
                    }
                }

                Row(modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 30.dp, start = 20.dp, end = 20.dp)) {
                    Button(onClick = { /*TODO*/ //存草稿
                    }, modifier = Modifier.fillMaxWidth(0.3f).padding(end = 10.dp)) {
                        Text(text = "保存")
                    }
                    Button(onClick = {
                        vm.publish { CuLog.debug(CuTag.Publish, "发布成功") }
                    }, modifier = Modifier.fillMaxWidth()) {
                        Text(text = "发布")
                    }
                }


                //            if (showDialog) {
                //                val pageType = listOf("文本", "图文", "视文")
                //                Dialog(
                //                    onDismissRequest = {
                ////                        showDialog = false
                //                    }
                //                ) {
                //                    // 对话框的内容
                //                    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
                //                        pageType.forEach { _ ->
                //                            Button(onClick = {
                ////                                dialogResult = it
                //                            }) {
                //                                Text("确定")
                //                            }
                //                        }
                //                    }
                //                }
                //            }
            } else {
                LaunchedEffect(Unit) {
                    permissionState.launchMultiplePermissionRequest()
                }
            }
        }
    }
}

@Composable
fun VisibilityRow(visibility: Visibility, tapFn: () -> Unit) {
    Row(modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 30.dp).fillMaxWidth()
        .clickableWithoutRipple {
            tapFn()
        }, horizontalArrangement = Arrangement.SpaceBetween) {
        Icon(Icons.Filled.Lock, contentDescription = null)
        Text(text = Visibility.getUiVisibility(visibility = visibility))
        Box(modifier = Modifier.width(60.dp), contentAlignment = Alignment.CenterEnd) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
fun Follow(clickFn: () -> Unit) {
    Row(modifier = Modifier.padding(start = 20.dp, end = 20.dp, bottom = 30.dp).fillMaxWidth()
        .clickable {
            clickFn()
        },
        horizontalArrangement = Arrangement.SpaceBetween) { //        Icon(R.drawable, contentDescription = null, tint = Color.Gray)
        SvgIcon(path = "svg/follow_blog.svg",
            contentDescription = "",
            modifier = Modifier.size(20.dp).padding(end = 5.dp))
        Text(text = stringResource(id = R.string.publish_option_follow), color = Color.Gray)
        Box(modifier = Modifier.width(60.dp))
    }
}

@Composable
fun TopBar(backTapFn: () -> Unit) {
    Row(horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(top = 60.dp).fillMaxWidth()) {
        BackButton {
            backTapFn()
        }
        Text("文字", fontWeight = FontWeight.Bold)
        Box(modifier = Modifier.width(60.dp)) {}
    }
}

@Composable
fun MediaBox(pictureList: List<Uri>, selectUri: (Uri) -> Unit, addPicture: (List<Uri>) -> Unit, coverPath: Uri? = null) {
    Row(Modifier.fillMaxWidth()) {
        if (coverPath != null) Box(Modifier.padding(top = 10.dp,
            bottom = 10.dp,
            start = 10.dp)) { VideoBox(coverPath) {} }
        LazyRow(Modifier //                .fillMaxWidth()
            .padding(top = 10.dp, bottom = 10.dp),
            contentPadding = PaddingValues(start = if (coverPath == null) 15.dp else 0.dp)) {
            items(pictureList.size) { index ->
                PictureBox(uri = pictureList[index], tapFn = {
                    selectUri(it)
                })
            }
        }

        ImagePicker(onSelected = {}) {
            Box(Modifier.padding(top = 10.dp)) {
                AddPictureBox {}
            }
        }
    }
}

@Composable
fun AddPictureBox(tapFn: () -> Unit) {
    Surface(shape = RoundedCornerShape(10.dp),
        modifier = Modifier.width(60.dp).height(80.dp).padding(5.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))) {
        Icon(Icons.Filled.Add, contentDescription = "", modifier = Modifier.size(15.dp))
    }
}

@Composable
fun PictureBox(uri: Uri, tapFn: (Uri) -> Unit) {
    Surface(shape = RoundedCornerShape(10.dp),
        modifier = Modifier.width(60.dp).height(80.dp).padding(5.dp).clickable { tapFn(uri) }) {
        AsyncImage(model = uri, contentDescription = "", contentScale = ContentScale.FillBounds)
    }
}

@Composable
fun VideoBox(uri: Uri, tapFn: (Uri) -> Unit) {
    Surface(shape = RoundedCornerShape(10.dp),
        modifier = Modifier.width(60.dp).height(80.dp).padding(5.dp).clickable { tapFn(uri) }) {
        AsyncImage(model = uri, contentDescription = "", contentScale = ContentScale.FillBounds)
        Icon(Icons.Filled.PlayArrow, contentDescription = "", modifier = Modifier.size(20.dp))
    }
}

@Composable
fun InputBox(hasMedia: Boolean = false, content: String, addPicture: (List<Uri>) -> Unit, updateContent: (String) -> Unit) { //    OutlinedTextField(modifier = Modifier
    //        .fillMaxWidth()
    //        .padding(5.dp),
    //        colors = OutlinedTextFieldDefaults.colors(
    //            unfocusedContainerColor = Color.Transparent,
    //            unfocusedBorderColor = Color.Transparent,
    //            focusedBorderColor = Color.Transparent,
    //            unfocusedLabelColor = Color.LightGray
    //        ),
    //        value = title,
    //        label = { Text(text = "标题: (填写)") },
    //        onValueChange = {
    //            updateTitle(it)
    //        })

    Column {
        OutlinedTextField(modifier = Modifier.fillMaxWidth() //            .fillMaxHeight()
            .height(if (hasMedia) 200.dp else 300.dp).padding(5.dp),
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                unfocusedLabelColor = Color.LightGray),
            value = content,
            label = { Text(text = "想写你就多写点") },
            maxLines = 10,
            onValueChange = {
                updateContent(it)
            })

        Row(modifier = Modifier.fillMaxWidth().padding(end = 10.dp),
            horizontalArrangement = Arrangement.End) {
            ImagePicker(onSelected = {
                addPicture(it)
            }) {
                Icon(Icons.Filled.Menu, contentDescription = "", modifier = Modifier.size(30.dp))
            }
        }
    }

}

@Composable
fun Options(title: String, iconPath: String, modifier: Modifier = Modifier, selected: Boolean = false, tapFn: () -> Unit) {
    TextButton(onClick = tapFn,
        colors = ButtonDefaults.buttonColors(containerColor = if (selected) Color(0xff333333) else Color(
            0xffeeeeee), contentColor = if (selected) Color.White else Color.Black),
        contentPadding = PaddingValues(top = 1.dp, bottom = 1.dp, start = 15.dp, end = 15.dp),
        shape = CircleShape,
        modifier = Modifier.padding(end = 10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SvgIcon(path = iconPath,
                contentDescription = "",
                modifier = modifier.size(20.dp).padding(end = 5.dp))
            Text(text = title, maxLines = 1, softWrap = false, overflow = TextOverflow.Ellipsis)
        }
    }
}


@Composable
fun OptionDialog(showDialog: Boolean, onDismiss: () -> Unit, content: @Composable () -> Unit) {
    if (showDialog) {
        var isActiveClose by remember { mutableStateOf(false) }
        AnyPopDialog(modifier = Modifier.fillMaxWidth()
            .requiredHeightIn(max = ScreenUtils.screenHeight.times(0.6).dp,
                min = 80.dp) //                .fillMaxHeight(0.6f)
            .background(color = Color.White).clickable(indication = rememberRipple(),
                interactionSource = remember { MutableInteractionSource() },
                onClick = {
                    isActiveClose = true
                }), isActiveClose = isActiveClose, // 请根据自己需要自己配置，自己定制谢谢配合
            properties = AnyPopDialogProperties(direction = DirectionState.BOTTOM,
                dismissOnClickOutside = true,
                backgroundDimEnabled = false, // 你自己设置哦
                navBarColor = MaterialTheme.colorScheme.background), content = {
                content()
            }, onDismiss = onDismiss)
    }
}

@Composable
fun FollowOptions(currentFollowable: Followable, setFollowFn: (Followable) -> Unit) {
    Text("动态跟随权限", modifier = Modifier.padding(start = 20.dp), fontWeight = FontWeight.Bold)
    LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
        items(Followable.entries.size) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 20.dp)
                .clickable {
                    setFollowFn(Followable.entries[it])
                }) {
                Text(Followable.getUiFollowable(followable = Followable.entries[it]),
                    fontWeight = if (currentFollowable == Followable.entries[it]) FontWeight.Bold else FontWeight.Normal)
            }
        }
    }
}

@Composable
fun VisibilityOptions(currentVisibility: Visibility, setVisibilityFn: (Visibility) -> Unit) {
    Text("博文可见性", modifier = Modifier.padding(start = 20.dp), fontWeight = FontWeight.Bold)
    LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
        items(Visibility.entries.size) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 20.dp)
                .clickable {
                    setVisibilityFn(Visibility.entries[it])
                }) {
                Text(Visibility.getUiVisibility(visibility = Visibility.entries[it]),
                    fontWeight = if (currentVisibility == Visibility.entries[it]) FontWeight.Bold else FontWeight.Normal)
            }
        }
    }
}

@Composable
fun TopicOptions(tags: List<BlogTagDto>, tapTopicFn: (BlogTagDto) -> Unit) {
    Text("标签", modifier = Modifier.padding(start = 20.dp), fontWeight = FontWeight.Bold)
    LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
        items(tags.size) {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 20.dp)
                .clickable {
                    tapTopicFn(tags[it])
                }) {
                Text(tags[it].name)
            }
        }
    }
}

@Composable
fun AtOptions(users: List<UserDto>, tapUserFn: (UserDto) -> Unit) { //    Text("标签", modifier = Modifier.padding(start = 20.dp), fontWeight = FontWeight.Bold)
    LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
        items(users) { item ->
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 20.dp)
                .clickable {
                    tapUserFn(item)
                }) {

                Row {
                    Avatar(item.profile)
                    Text(item.nickname)
                    Text("最近@次数：${item.ats}")
                }

            }
        }
    }
}

@Composable
fun MediaOptions(uri: Uri, editFn: (Uri) -> Unit, removeFn: (Uri) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp).clickable {
            editFn(uri)
        }) {
            Text("编辑")
        }
        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp).clickable {
            removeFn(uri)
        }) {
            Text(text = "删除")
        }
    }
}