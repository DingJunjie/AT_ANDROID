package com.bitat.ui.publish

//noinspection UsingMaterialAndMaterial3Libraries
import android.Manifest
import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.bitat.R
import com.bitat.ext.clickableWithoutRipple
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.Commentable
import com.bitat.repository.consts.Followable
import com.bitat.repository.consts.PublishSettings
import com.bitat.repository.consts.Visibility
import com.bitat.repository.dto.resp.BlogTagDto
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.router.AtNavigation
import com.bitat.ui.common.AnyPopDialog
import com.bitat.ui.common.AnyPopDialogProperties
import com.bitat.ui.common.DirectionState
import com.bitat.ui.common.ImagePicker
import com.bitat.ui.common.ImagePickerOption.ImageOnly
import com.bitat.ui.common.ImagePickerOption.VideoOnly
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.common.rememberDialogState
import com.bitat.ui.component.BackButton
import com.bitat.ui.theme.Typography
import com.bitat.ui.video.VideoPlayer
import com.bitat.utils.GaoDeUtils
import com.bitat.utils.ScreenUtils
import com.bitat.viewModel.PublishViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.wordsfairy.note.ui.widgets.toast.ToastModel
import com.wordsfairy.note.ui.widgets.toast.showToast

enum class PublishTextOption {
    Topic, At, Follow, Font, Visibility, None, Media, Location, Settings, Comment, Pick
}

@OptIn(ExperimentalPermissionsApi::class, ExperimentalFoundationApi::class)
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun PublishDetailPage(navHostController: NavHostController, viewModelProvider: ViewModelProvider) {
    val vm = viewModelProvider[PublishViewModel::class]
    val commonState by vm.commonState.collectAsState()
    val mediaState by vm.mediaState.collectAsState()
    val showDialog by remember { mutableStateOf(false) }
    val dialogResult by rememberSaveable { mutableStateOf("") }
    val myTextFieldValue by remember { mutableStateOf(TextFieldValue("")) }

    val focusRequester = remember {
        FocusRequester()
    }
    val focusManager = LocalFocusManager.current

    var option by remember {
        mutableStateOf(PublishTextOption.None)
    }

    val bottomOptHeight = remember {
        mutableIntStateOf(0)
    }

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(
                vm.commonState.value.content
            )
        )
    }

    var topicKeyword by remember {
        mutableStateOf("")
    }

    var userKeyword by remember {
        mutableStateOf("")
    }

    val currentTopicList: SnapshotStateList<BlogTagDto> = remember {
        mutableStateListOf()
    }

    val currentUserList: SnapshotStateList<UserBase1Dto> = remember {
        mutableStateListOf()
    }

    fun updateTopicKeyword(kw: String) {
        topicKeyword = kw
        vm.searchTag(kw)
    }

    fun updateUserKeyword(kw: String) {
        userKeyword = kw
        vm.searchAt(kw)
    }

    val dialog = rememberDialogState()

    val selectedUri = remember {
        mutableStateOf(Uri.EMPTY)
    }
    val tagStart = remember {
        mutableStateOf(-1)
    }
    val tagEnd = remember {
        mutableStateOf(-1)
    }

    val inputTag = remember {
        mutableStateOf("")
    }

    val atStart = remember {
        mutableStateOf(-1)
    }
    val atEnd = remember {
        mutableStateOf(-1)
    }
    val inputAt = remember {
        mutableStateOf("")
    }

    fun onContentChange(content: String, cursorOffset: Int = -1) {

    }


    var showOptDialog by remember { mutableStateOf(false) }
    OptionDialog(
        showOptDialog,
        onDismiss = { showOptDialog = false; option = PublishTextOption.None }) {
        if (option == PublishTextOption.Follow) {
            FollowOptions(
                currentFollowable = Followable.getFollowable(commonState.followId),
                setFollowFn = {
                    vm.onFollowClick(it)
                    option = PublishTextOption.None
                    showOptDialog = false
                })
        } else if (option == PublishTextOption.Visibility) {

            VisibilityOptions(currentVisibility = commonState.visibility, setVisibilityFn = {
                vm.onVisibilityClick(it)
                option = PublishTextOption.None
                showOptDialog = false
            })
        } else if (option == PublishTextOption.Media) {
            MediaOptions(selectedUri.value, editFn = {

            }, removeFn = {
                vm.removeMedia(it)
                option = PublishTextOption.None
                showOptDialog = false
            })
        } else if (option == PublishTextOption.Comment) {
            CommentOptions(currentComment = commonState.commentable, setCommentFn = {
                vm.onCommentableClick(it)
                option = PublishTextOption.None
                showOptDialog = false
            })
        } else if (option == PublishTextOption.Settings) {
            SettingsOptions(tapFn = {
                option = PublishTextOption.None
                showOptDialog = false
            })
        } else if (option == PublishTextOption.Pick) {
            PickOptions(imgFn = {
                if (it.isNotEmpty()){
                    vm.addPicture(it)
                }
                option = PublishTextOption.None
                showOptDialog = false
            }, videoFn = {
                if (it.isNotEmpty()) vm.addVideo(it[0])
                option = PublishTextOption.None
                showOptDialog = false
            })
        }
    }

    // 点击按钮事件，非话题、at是不同的显示
    fun tapOption(opt: PublishTextOption) {
        if (option == opt) {
            option = PublishTextOption.None
            showOptDialog = false
        } else {
            option = opt
        }

        when (opt) {
            PublishTextOption.Topic -> {
                // focusRequester.requestFocus()

                // var contentSplit = commonState.content.split("")
                // contentSplit = contentSplit.subList(1, contentSplit.size - 1)
                // if (commonState.content.isEmpty()) {
                //     // 没有内容，直接添加 #
                //     vm.onContentChange(commonState.content + "#")
                //     textFieldValue = textFieldValue.copy(
                //         text = vm.commonState.value.content,
                //         selection = TextRange(vm.commonState.value.content.length)
                //     )
                // } else if (textFieldValue.selection.start == contentSplit.size) {
                //     // 在最后添加 #， 如果最后已经是 #了则关闭，如果不是则添加
                //     if (contentSplit.last() == "#") {
                //         vm.onContentChange(
                //             commonState.content.substring(
                //                 0,
                //                 commonState.content.length - 1
                //             )
                //         )
                //         textFieldValue = textFieldValue.copy(
                //             text = vm.commonState.value.content,
                //             selection = TextRange(vm.commonState.value.content.length)
                //         )
                //     } else {
                //         vm.onContentChange(commonState.content + "#")
                //         textFieldValue = textFieldValue.copy(
                //             text = vm.commonState.value.content,
                //             selection = TextRange(vm.commonState.value.content.length)
                //         )
                //     }
                // } else {
                //     // 在中间添加 #，根据光标分割，前组的最后添加 #，如果最后是 # 则删除，如果不是则添加
                //     var before = contentSplit.subList(0, textFieldValue.selection.start)
                //     val after =
                //         contentSplit.subList(textFieldValue.selection.start, contentSplit.size)
                //     if (before.isEmpty()&&before.last() == "#") {
                //         before = before.subList(0, before.size - 1)
                //         vm.onContentChange(
                //             before.joinToString("") + after.joinToString("")
                //         )
                //         textFieldValue = textFieldValue.copy(
                //             text = commonState.content,
                //             selection = TextRange(before.size)
                //         )
                //     } else {
                //         vm.onContentChange(before.joinToString("") + "#" + after.joinToString(""))
                //         textFieldValue = textFieldValue.copy(
                //             text = vm.commonState.value.content,
                //             selection = TextRange(before.size + 1)
                //         )
                //     }
                // }
                vm.initTags()
            }

            PublishTextOption.At -> {
                vm.initAt()
            }

            else -> {
                showOptDialog = true
            }
        }
    }


//    Scaffold(
//        topBar = {
//            TopBar(backTapFn = {
//                navHostController.popBackStack()
//            })
//        }, modifier = Modifier
//            .fillMaxSize()
//            .background(Color.White)
//    ) { padding ->


    Column(verticalArrangement = Arrangement.Top, modifier = Modifier.fillMaxSize()) {
        TopBar(backTapFn = {
            navHostController.popBackStack()
        })
        if (permissionState.allPermissionsGranted) {
            Column(verticalArrangement = Arrangement.Top) {
                //                    mediaState.localImages.add(mediaState.localVideo)
                //                    if ( mediaState.localVideo != Uri.EMPTY&& mediaState.localImages.isEmpty()){
                //                        vm.addPicture(arrayListOf(mediaState.localVideo))
                //                    }

                if (mediaState.localImages.isNotEmpty() || mediaState.localVideo != Uri.EMPTY) MediaBox(
                    mediaState.localImages,
                    selectUri = {
                        selectedUri.value = it
                        option = PublishTextOption.Media
                        showOptDialog = true
                    },
                    addPicture = {
                        option = PublishTextOption.Pick
                        showOptDialog = true
                    },
                    coverPath = mediaState.localVideo
                )

                InputBox(hasMedia = mediaState.localImages.isNotEmpty() || mediaState.localVideo != Uri.EMPTY,
                    textFieldValue,
//                        commonState.content,
                    focusRequester,
                    showImg = {
                        option = PublishTextOption.Pick
                        showOptDialog = true
                    },
                    onValueChange = {
                        onContentChange(it.text, it.selection.start)
                        textFieldValue = it
                        vm.onContentChange(it.text)
                    })


                Row(horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.Top,
                    modifier = Modifier
                        .padding(start = 20.dp)
                        .onGloballyPositioned {
//                                bottomOptHeight.intValue =
//                                    ScreenUtils.screenHeight - (it.positionInWindow().y / Density).toInt()
                        }) {
                    Options(title = stringResource(id = R.string.publish_option_topic),
                        iconPath = "svg/topic.svg",
                        selected = option == PublishTextOption.Topic,
                        modifier = Modifier.height(30.dp),
                        tapFn = {
                            tapOption(PublishTextOption.Topic)
                        })

                    Options(title = stringResource(id = R.string.publish_option_at),
                        iconPath = "svg/at.svg",
                        selected = option == PublishTextOption.At,
                        modifier = Modifier.height(30.dp),
                        tapFn = {
                            tapOption(PublishTextOption.At)
                        })

                    Options(title = if (commonState.location == "") stringResource(id = R.string.publish_location_add) else commonState.location,
                        iconPath = "svg/location_line.svg",
                        selected = option == PublishTextOption.Location,
                        modifier = Modifier.height(30.dp),
                        tapFn = {
                            GaoDeUtils.getLocation() { point, name ->
                                vm.locationUpdate(point, name)
                            }
                        })

                }

                HorizontalDivider(
                    color = Color(0xffeeeeee),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 10.dp)
                )

                FollowRow() {
                    tapOption(PublishTextOption.Follow)
                }

                CommentableRow(commentable = commonState.commentable) {
                    tapOption(PublishTextOption.Comment)
                }

                VisibilityRow(commonState.visibility) {
                    tapOption(PublishTextOption.Visibility)
                }

                SettingsRow(tapFn = {
                    tapOption(PublishTextOption.Settings)
                })
            }

        } else {
            LaunchedEffect(Unit) {
                permissionState.launchMultiplePermissionRequest()
            }
        }

        Box {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 30.dp, start = 20.dp, end = 20.dp)
            ) {
                Button(
                    onClick = { /*TODO*/
                        vm.saveDraft()
                    }, modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .padding(end = 10.dp)
                ) {
                    Text(text = "保存")
                }
                Button(onClick = {
                    vm.publish {
                        ToastModel("发布成功！", ToastModel.Type.Success).showToast()
                        vm.clearData()
                        AtNavigation(navHostController).navigateToHome()
                    }
                }, modifier = Modifier.fillMaxWidth(), enabled = commonState.isPublishClick) {
                    Text(text = "发布")
                }
            }
        }
    }

    if (option == PublishTextOption.Topic) {
        CommonSheet<BlogTagDto>(currentTopicList, dismissTap = {
            option = PublishTextOption.None
        }, topicKeyword, updateKeyword = {
            updateTopicKeyword(it)
        }, remove = {
            currentTopicList.remove(it)
        }, addToList = {
            val newOffset = vm.addTopicsToTopic(currentTopicList, textFieldValue.selection.start)
            textFieldValue = textFieldValue.copy(
                text = vm.commonState.value.content, selection = TextRange(newOffset)
            )
            option = PublishTextOption.None
            currentTopicList.clear()
        }, optionList = {
            CommonOptions<BlogTagDto>(
                input = topicKeyword,
                items = commonState.tagSearchResult,
                tapFn = {
//                    vm.refreshTags()
                    CuLog.info(
                        CuTag.Publish, "start is" + textFieldValue.selection.start.toString()
                    )

                    if (vm.commonState.value.tags.size + currentTopicList.size >= 5) {
                        dialog.show("添加话题失败", "一次性最多添加5个话题")
                        return@CommonOptions
                    }

                    if (currentTopicList.contains(it)) {
                        return@CommonOptions
                    }

                    currentTopicList.add(it)
                })
        })
    } else if (option == PublishTextOption.At) {
        CommonSheet<UserBase1Dto>(currentUserList, dismissTap = {
            option = PublishTextOption.None
        }, topicKeyword, updateKeyword = {
            updateTopicKeyword(it)
        }, remove = {
            currentUserList.remove(it)
        }, addToList = {
            val newOffset = vm.addUsersToAt(currentUserList, textFieldValue.selection.start)
            textFieldValue = textFieldValue.copy(
                text = vm.commonState.value.content, selection = TextRange(newOffset)
            )
            option = PublishTextOption.None
            currentUserList.clear()
        }, optionList = {
            CommonOptions<UserBase1Dto>(
                input = userKeyword,
                items = commonState.atUserSearchResult,
                tapFn = {
                    CuLog.info(
                        CuTag.Publish, "start is" + textFieldValue.selection.start.toString()
                    )

                    if (currentUserList.size >= 5) {
                        dialog.show("添加用户失败", "一次性最多添加5个用户")
                        return@CommonOptions
                    }

                    if (currentUserList.contains(it)) {
                        return@CommonOptions
                    }

                    currentUserList.add(it)
                })
        })
    }
}

@Composable
inline fun <reified T> CommonSheet(
    currentList: List<T>,
    crossinline dismissTap: () -> Unit,
    keyword: String,
    noinline updateKeyword: (String) -> Unit,
    noinline remove: (T) -> Unit,
    crossinline addToList: () -> Unit,
    optionList: @Composable () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color(0x33333333))
            .clickable {
                dismissTap()
            }, contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier
                .background(Color.White)
                .border(
                    width = 0.dp, shape = RoundedCornerShape(20.dp), color = Color.Transparent
                )
                .padding(top = 15.dp)
                .fillMaxHeight(0.8f)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "话题选择",
                    style = Typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(start = 20.dp)
                )

                Text(text = "添加", modifier = Modifier.clickable {
                    addToList()
                })
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                SearchField(keyword, updateKeyword)
            }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                items(currentList.toMutableList()) { item ->
                    Surface(modifier = Modifier.padding(end = 6.dp)) {
                        CommonChip(item, remove)
                    }
                }
            }
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                optionList()
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
inline fun <reified T> CommonChip(item: T, crossinline removeItem: (T) -> Unit) {
    Card(shape = RoundedCornerShape(10.dp)) {
        FlowRow(
            modifier = Modifier.padding(vertical = 2.dp, horizontal = 4.dp)
//                .background(Color.LightGray)
        ) {
            Text(
                if (item is UserBase1Dto) item.nickname else if (item is BlogTagDto) item.name else "",
                modifier = Modifier.padding(start = 4.dp, end = 2.dp)
            )
            Icon(Icons.Filled.Close,
                contentDescription = "",
                modifier = Modifier.clickable { removeItem(item) })
        }
    }
}

@Composable
fun SearchField(keyword: String, updateKeyword: (String) -> Unit) {
    BasicTextField(value = keyword,
        onValueChange = { updateKeyword(it) },
        singleLine = true,
        modifier = Modifier
            .padding(vertical = 10.dp)
            .height(36.dp)
            .fillMaxWidth(),
        textStyle = TextStyle(
            fontSize = 16.sp, lineHeight = 36.sp
        ),
        decorationBox = { innerTextField ->
            Box(
                Modifier
                    .border(2.dp, Color.Gray, RoundedCornerShape(10.dp))
                    .padding(vertical = 6.dp, horizontal = 8.dp)
            ) {
                if (keyword.isEmpty()) {
                    Text(
                        "请输入想要搜索的话题",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        lineHeight = 24.sp
                    )
                }
                innerTextField()  // 显示实际的文本输入框
            }
        })
}


@Composable
fun VisibilityRow(visibility: Visibility, tapFn: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, bottom = 30.dp)
            .fillMaxWidth()
            .clickableWithoutRipple {
                tapFn()
            }, horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(Icons.Filled.Lock, contentDescription = null)
        Text(text = Visibility.getUiVisibility(visibility = visibility))
        Box(modifier = Modifier.width(60.dp), contentAlignment = Alignment.CenterEnd) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
fun SettingsRow(tapFn: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, bottom = 30.dp)
            .fillMaxWidth()
            .clickableWithoutRipple {
                tapFn()
            }, horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(Icons.Filled.Lock, contentDescription = null)
        Text(text = "设置")
        Box(modifier = Modifier.width(60.dp), contentAlignment = Alignment.CenterEnd) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
fun CommentableRow(commentable: Commentable, tapFn: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, bottom = 30.dp)
            .fillMaxWidth()
            .clickableWithoutRipple {
                tapFn()
            }, horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Icon(Icons.Filled.Lock, contentDescription = null)
        Text(text = Commentable.getUiCommentable(commentable = commentable))
        Box(modifier = Modifier.width(60.dp), contentAlignment = Alignment.CenterEnd) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
fun FollowRow(clickFn: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, bottom = 30.dp)
            .fillMaxWidth()
            .clickable {
                clickFn()
            }, horizontalArrangement = Arrangement.SpaceBetween
    ) { //        Icon(R.drawable, contentDescription = null, tint = Color.Gray)
        SvgIcon(
            path = "svg/follow_blog.svg",
            contentDescription = "",
            modifier = Modifier
                .size(20.dp)
                .padding(end = 5.dp)
        )
        Text(text = stringResource(id = R.string.publish_option_follow), color = Color.Gray)
        Box(modifier = Modifier.width(60.dp))
    }
}

@Composable
fun TagsRow(tags: List<BlogTagDto>) {
    LazyRow(
        modifier = Modifier.padding(10.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(tags) { item ->
            Column(
                modifier = Modifier
                    .border(1.dp, Color.Black, RoundedCornerShape(20.dp))
                    .padding(all = 10.dp)
            ) {
                Text(text = "#${item.name}")
            }
        }
    }
}

@Composable
fun TopBar(backTapFn: () -> Unit) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top = 60.dp)
            .fillMaxWidth()
    ) {
        BackButton {
            backTapFn()
        }
        Text("创作", fontWeight = FontWeight.Bold)
        Box(modifier = Modifier.width(60.dp)) {}
    }
}

@Composable
fun MediaBox(
    pictureList: List<Uri>,
    selectUri: (Uri) -> Unit,
    addPicture: () -> Unit,
    coverPath: Uri = Uri.EMPTY
) {
    val contentResolver = LocalContext.current.contentResolver
    Row(Modifier.fillMaxWidth()) {
        if (coverPath != Uri.EMPTY) Box(
            Modifier.padding(
                top = 10.dp, bottom = 10.dp, start = 10.dp, end = 10.dp
            )
        ) {
            VideoBox(videoUri = coverPath, tapFn = {
                selectUri(it)
            })
        }
        LazyRow(
            Modifier //                .fillMaxWidth()
                .padding(top = 10.dp, bottom = 10.dp),
            contentPadding = PaddingValues(start = if (coverPath == Uri.EMPTY) 15.dp else 0.dp),horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            items(pictureList) { pic ->
                PictureBox(uri = pic, tapFn = {
                    selectUri(pic)
                })
            }
        }

        Box(
            Modifier
                .padding(top = 10.dp, start = 10.dp)
                .clickable { addPicture() }) {
            AddPictureBox {}
        }
    }
}

@Composable
fun AddPictureBox(tapFn: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .width(80.dp)
            .height(100.dp)
            .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
    ) {
        Icon(Icons.Filled.Add, contentDescription = "", modifier = Modifier.size(15.dp))
    }
}

@Composable
fun PictureBox(uri: Uri, tapFn: (Uri) -> Unit) {
    Surface(shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .width(80.dp)
            .height(100.dp)
            .clickable { tapFn(uri) }) {
        AsyncImage(model = uri, contentDescription = "", contentScale = ContentScale.FillBounds)
    }
}

@Composable
fun VideoBox(modifier: Modifier = Modifier, videoUri: Uri, tapFn: (Uri) -> Unit) {
    Surface(shape = RoundedCornerShape(10.dp),
        modifier = modifier
            .width(80.dp)
            .height(100.dp)
            .clickable { tapFn(videoUri) }) { //        AsyncImage(model = uri, contentDescription = "", contentScale = ContentScale.FillBounds)
        VideoPlayer(uri = videoUri)
    }
}

//@Composable
//fun VideoBox(uri: Uri, tapFn: (Uri) -> Unit) {
//    Surface(shape = RoundedCornerShape(10.dp),
//        modifier = Modifier.width(60.dp).height(80.dp).padding(5.dp).clickable { tapFn(uri) }) {
//        AsyncImage(model = uri, contentDescription = "", contentScale = ContentScale.FillBounds)
//        Icon(Icons.Filled.PlayArrow, contentDescription = "", modifier = Modifier.size(20.dp))
//    }
//}

@Composable
fun InputBox(
    hasMedia: Boolean = false,
    textFieldValue: TextFieldValue,
    focusRequester: FocusRequester,
    showImg: () -> Unit,
    onValueChange: (TextFieldValue) -> Unit
) {
    //    OutlinedTextField(modifier = Modifier
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

    //    var myTextFieldValue by remember { mutableStateOf(textFieldValue) }

    Column {
        OutlinedTextField(
            textFieldValue,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth() //            .fillMaxHeight()
                .height(if (hasMedia) 100.dp else 200.dp)
                .padding(5.dp)
                .focusRequester(focusRequester),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = Color.Transparent,
                unfocusedLabelColor = Color.LightGray
            ),
            label = { Text(text = "想写你就多写点") },
            maxLines = 10,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 10.dp)
                .clickable { showImg() },
            horizontalArrangement = Arrangement.End
        ) {
            Icon(Icons.Filled.Menu, contentDescription = "", modifier = Modifier.size(30.dp))
        }
    }

}

@Composable
fun Options(
    title: String,
    iconPath: String,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    tapFn: () -> Unit
) {
    TextButton(
        onClick = tapFn, colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) Color(0xff333333) else Color(
                0xffeeeeee
            ), contentColor = if (selected) Color.White else Color.Black
        ), shape = CircleShape, modifier = modifier.padding(end = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SvgIcon(
                path = iconPath, contentDescription = "", modifier = Modifier.padding(end = 5.dp)
            )
            Text(
                text = title,
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(fontSize = 12.sp)
            )
        }
    }
}


@Composable
fun OptionDialog(showDialog: Boolean, onDismiss: () -> Unit, content: @Composable () -> Unit) {
    if (showDialog) {
        var isActiveClose by remember { mutableStateOf(false) }
        AnyPopDialog(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeightIn(
                    max = ScreenUtils.screenHeight.times(0.6).dp, min = 80.dp
                ) //                .fillMaxHeight(0.6f)
                .background(color = Color.White)
                .clickable(
                    onClick = {
                        isActiveClose = true
                    }), isActiveClose = isActiveClose, // 请根据自己需要自己配置，自己定制谢谢配合
            properties = AnyPopDialogProperties(
                direction = DirectionState.BOTTOM,
                dismissOnClickOutside = true,
                backgroundDimEnabled = false, // 你自己设置哦
                navBarColor = MaterialTheme.colorScheme.background
            ), content = {
                content()
            }, onDismiss = onDismiss
        )
    }
}

@Composable
fun FollowOptions(currentFollowable: Followable, setFollowFn: (Followable) -> Unit) {
    Text("动态跟随权限", modifier = Modifier.padding(start = 20.dp), fontWeight = FontWeight.Bold)
    LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
        items(Followable.entries.size) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 20.dp)
                .clickable {
                    setFollowFn(Followable.entries[it])
                }) {
                Text(
                    Followable.getUiFollowable(followable = Followable.entries[it]),
                    fontWeight = if (currentFollowable == Followable.entries[it]) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun VisibilityOptions(currentVisibility: Visibility, setVisibilityFn: (Visibility) -> Unit) {
    Text("博文可见性", modifier = Modifier.padding(start = 20.dp), fontWeight = FontWeight.Bold)
    LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
        items(Visibility.entries.size) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 20.dp)
                .clickable {
                    setVisibilityFn(Visibility.entries[it])
                }) {
                Text(
                    Visibility.getUiVisibility(visibility = Visibility.entries[it]),
                    fontWeight = if (currentVisibility == Visibility.entries[it]) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun CommentOptions(currentComment: Commentable, setCommentFn: (Commentable) -> Unit) {
    Text("评论权限", modifier = Modifier.padding(start = 20.dp), fontWeight = FontWeight.Bold)
    LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
        items(Commentable.entries.size) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 20.dp)
                .clickable {
                    setCommentFn(Commentable.entries[it])
                }) {
                Text(
                    Commentable.getUiCommentable(commentable = Commentable.entries[it]),
                    fontWeight = if (currentComment == Commentable.entries[it]) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@Composable
fun SettingsOptions(tapFn: () -> Unit) {
    LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
        items(PublishSettings.entries) {
            Box(modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 20.dp)
                .clickable {
                    tapFn()
                }) {
                Text(PublishSettings.getUiTitle(settings = it))
            }
        }
    }
}

@Composable
inline fun <reified T> CommonOptions(
    input: String, items: List<T>, crossinline tapFn: (T) -> Unit
) {
    Surface(shape = RoundedCornerShape(10.dp)) {
        Column {
            if (input.isNotEmpty() && items.isNotEmpty() && items[0] != input && items[0] is BlogTagDto) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp, horizontal = 20.dp)
                    .clickable {
                        val newItem = BlogTagDto().apply {
                            id = -1
                            name = input
                        }
                        tapFn(newItem as T)
                    }) {
                    Text(input)
                }
            }
            LazyColumn(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxHeight()
            ) {

                items(items) { item ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 20.dp)
                        .clickable {
                            tapFn(item)
                        }) {
                        Text(if (item is UserBase1Dto) item.nickname else if (item is BlogTagDto) item.name else "")
                    }
                }
            }
        }
    }
}

@Composable
fun MediaOptions(uri: Uri, editFn: (Uri) -> Unit, removeFn: (Uri) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable {
                editFn(uri)
            }) {
            Text("编辑")
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable {
                removeFn(uri)
            }) {
            Text(text = "删除")
        }
    }
}

@Composable
fun PickOptions(imgFn: (List<Uri>) -> Unit, videoFn: (List<Uri>) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable {

            }) {
            ImagePicker(20, ImageOnly, onSelected = {
                imgFn(it)
            }) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        Icons.Filled.Menu, contentDescription = "", modifier = Modifier.size(30.dp)
                    )
                    Text(text = "图片")
                }
            }
        }
        Box(modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable {

            }) {

            ImagePicker(1, VideoOnly, onSelected = {
                videoFn(it)
            }) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    Icon(
                        Icons.Filled.PlayArrow,
                        contentDescription = "",
                        modifier = Modifier.size(30.dp)
                    )
                    Text(text = "视频")
                }
            }
        }
    }
}

