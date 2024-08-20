package com.bitat.ui.blog

import android.annotation.SuppressLint
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.substring
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.repository.consts.BLOG_VIDEO_ONLY
import com.bitat.repository.consts.BLOG_VIDEO_TEXT
import com.bitat.repository.dto.resp.UserBase1Dto
import com.bitat.repository.store.CommentStore
import com.bitat.router.AtNavigation
import com.bitat.router.Screen
import com.bitat.state.BlogMenuOptions
import com.bitat.state.BlogOperation
import com.bitat.state.CommentState
import com.bitat.state.MenuOptions
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.common.RefreshView
import com.bitat.ui.common.rememberLoadMoreState
import com.bitat.ui.component.AnimatedMenu
import com.bitat.ui.component.CommentList
import com.bitat.ui.component.CommentTextField
import com.bitat.ui.component.Popup
import com.bitat.ui.theme.white
import com.bitat.utils.ScreenUtils
import com.bitat.viewModel.BlogViewModel
import com.bitat.viewModel.CommentViewModel
import com.bitat.viewModel.ImagePreviewViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

/***
 * 首页的数据显示
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun BlogPage(
    modifier: Modifier, navController: NavHostController, viewModelProvider: ViewModelProvider
) {
    val vm: BlogViewModel = viewModelProvider[BlogViewModel::class]
    val imagePreviewVm: ImagePreviewViewModel = viewModelProvider[ImagePreviewViewModel::class]
    val state by vm.blogState.collectAsState()
    val pagerState: PagerState = rememberPagerState { 3 }
    val loadMoreState = rememberLoadMoreState {
        CuLog.debug(CuTag.Blog, "loadMoreState") //        vm.initBlogList(blogState.currentMenu)
    }

    val commentVm: CommentViewModel = viewModelProvider[CommentViewModel::class]
    val commentState by commentVm.commentState.collectAsState()

    var currentId by remember {
        mutableLongStateOf(0L)
    }
    LaunchedEffect(state.currentMenu) {
        vm.initBlogList(state.currentMenu)
    }

    var currentOperation by remember {
        mutableStateOf(BlogOperation.None)
    }

    val isOpen = remember {
        mutableStateOf(false)
    }

    val listState = rememberLazyListState()
    var previousIndex by remember { mutableStateOf(0) }

    val playingIndex = remember {
        mutableStateOf(0)
    }

    LaunchedEffect(listState) {    // 滚动事件监听
        snapshotFlow { listState.firstVisibleItemScrollOffset }.distinctUntilChanged()
            .collect { _ ->
                if (listState.layoutInfo.visibleItemsInfo.size > 1) {
                    if (listState.layoutInfo.visibleItemsInfo[1].offset < ScreenUtils.screenHeight.div(
                            3
                        ) && playingIndex.value != listState.firstVisibleItemIndex + 1
                    ) {
                        playingIndex.value = listState.firstVisibleItemIndex + 1
                    }
                }
            }

    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }.collect { _ ->
            CuLog.debug(
                CuTag.Blog,
                "previousIndex:$previousIndex,firstVisibleItemIndex;${listState.firstVisibleItemIndex}"
            )
            if (previousIndex < listState.firstVisibleItemIndex && state.topBarShow && previousIndex > 0) {
                vm.topBarState(false)
            } else if (previousIndex > listState.firstVisibleItemIndex && !state.topBarShow && previousIndex > 0) {
                vm.topBarState(true)
            }
            previousIndex = listState.firstVisibleItemIndex
        }
    }
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .collect { layoutInfo ->
                val lastVisibleItemIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index
                if (lastVisibleItemIndex == state.blogList.size - 1) {
                    vm.loadMore()
                }
            }
    }

    val coroutineScope = rememberCoroutineScope()

    val isCommentVisible = remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(white)
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            if (state.topBarShow) BlogTopBar(state.currentMenu,
                isOpen.value,
                { isOpen.value = it },
                switchMenu = { vm.switchBlogMenu(it) })
            RefreshView(modifier = Modifier
                .nestedScroll(loadMoreState.nestedScrollConnection)
                .padding(padding.calculateBottomPadding()),
                onRefresh = {
                    CuLog.debug(CuTag.Blog, "onRefresh 回调")
                    vm.initBlogList(state.currentMenu)
                }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    if (state.blogList.size > 0) {
                        if (state.blogList.first().kind.toInt() == BLOG_VIDEO_ONLY || state.blogList.first().kind.toInt() == BLOG_VIDEO_TEXT) {
                            currentId = state.blogList.first().id
                        }
                        LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                            itemsIndexed(state.blogList) { index, item -> //Text(item.content)
                                Surface(modifier = Modifier.fillMaxWidth()) {
                                    BlogItem(blog = item,
                                        isPlaying = playingIndex.value == index,
                                        navController, viewModelProvider = viewModelProvider,
                                        tapComment = {
                                            CommentStore.currentBlogId = item.id
                                            coroutineScope.launch {
                                                delay(1000)
                                                currentOperation = BlogOperation.Comment
                                            }
                                            isCommentVisible.value = true
                                        },
                                        tapAt = {
                                            coroutineScope.launch {
                                                delay(1000)
                                                currentOperation = BlogOperation.At
                                            }
                                        },
                                        tapLike = {},
                                        tapCollect = {
                                            coroutineScope.launch {
                                                currentOperation = BlogOperation.Collect
                                            }
                                        },
                                        contentClick = { item ->
                                            vm.setCurrentBlog(item)
                                            AtNavigation(navController).navigateToBlogDetail()
                                        }, moreClick = {
                                            vm.setCurrentBlog(item)
                                        })
                                }
                            }
                        }
                    } else {
                        when (state.currentMenu) {
                            BlogMenuOptions.Recommend -> Text(text = "推荐" + stringResource(R.string.no_data))
                            BlogMenuOptions.Latest -> Text(text = "最新" + stringResource(R.string.no_data))
                            BlogMenuOptions.Followed -> Text(text = "关注" + stringResource(R.string.no_data))
                        }

                    }
                }
            }
        }
    }

    CommentPopup(
        visible = isCommentVisible.value,
        blogId = CommentStore.currentBlogId,
        commentViewModel = commentVm,
        coroutineScope = coroutineScope,
        tapImage = {
            imagePreviewVm.setImagePreView(arrayOf(it))
            AtNavigation(navController).navigateToImagePreviewPage()
        },
        commentState = commentState,
        onClose = { isCommentVisible.value = false })
}


@Composable
fun BlogTopBar(
    currentMenu: BlogMenuOptions,
    isOpen: Boolean,
    toggleMenu: (Boolean) -> Unit,
    switchMenu: (BlogMenuOptions) -> Unit
) {
    Row(
        modifier = Modifier
            .height(30.dp)
            .padding(start = 5.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start
    ) {
        AnimatedMenu<BlogMenuOptions>(currentMenu, isOpen, toggleMenu) {
            switchMenu(it)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp, top = 5.dp, end = 10.dp, bottom = 5.dp),
            horizontalArrangement = Arrangement.End
        ) {
            SvgIcon(path = "svg/search.svg", tint = Color.Black, contentDescription = "")
        }
    }
}

@Composable
fun CommentPopup(
    visible: Boolean,
    commentViewModel: CommentViewModel,
    commentState: CommentState,
    coroutineScope: CoroutineScope,
    tapImage: (String) -> Unit,
    blogId: Long,
    onClose: () -> Unit
) {
    var textFieldValue by remember {
        mutableStateOf(
            TextFieldValue(commentState.commentInput)
        )
    }

    val focusRequester = remember {
        FocusRequester()
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
        if (cursorOffset == -1) return;
        if (content.isEmpty()) {
            atStart.value = -1
            return
        }

        val latestChar = content.split("")[cursorOffset]

        if (cursorOffset < atStart.value) {
            atStart.value = -1
            return
        }

        if (latestChar == "@" && atStart.value < 0) {
            atStart.value = cursorOffset - 1
            commentViewModel.searchUser("")
        } else if (latestChar == " " && atStart.value >= 0) {
            atEnd.value = cursorOffset - 1

            atStart.value = -1
            inputAt.value = ""
            commentViewModel.clearUserSearch()
        } else if (atStart.value >= 0) {
            inputAt.value = content.substring(atStart.value + 1, cursorOffset)
            commentViewModel.searchUser(inputAt.value)
        }
    }

    fun addAtUser(user: UserBase1Dto) {

        val result = commentViewModel.selectUser(user)
        if (textFieldValue.text.isEmpty()) {
            textFieldValue =
                textFieldValue.copy(text = "@${user.nickname} ")
            return
        }
        val textArr = textFieldValue.text.split("")
        val before = textArr.subList(0, textFieldValue.selection.start + 1)
        val afterStr = textArr.subList(textFieldValue.selection.start, textFieldValue.text.length)
            .joinToString("")

        val lastAtOffset = before.lastIndexOf("@")
        val beforeString = before.subList(0, lastAtOffset).joinToString("")

        val total = "$beforeString@${user.nickname}$afterStr "

        textFieldValue = textFieldValue.copy(text = total, selection = TextRange(total.length))

        commentViewModel.clearUserSearch()
    }

    Popup(visible, onClose = onClose) {
        CommentList(blogId, commentViewModel, commentState, tapImage = tapImage, tapContentFn = {
            commentViewModel.selectReplyComment(it)
        })

        Box(contentAlignment = Alignment.BottomCenter) {
            CommentTextField(
                textFieldValue,
                focusRequester = focusRequester,
                sendComment = {
                    coroutineScope.launch {
                        if (commentState.replyComment == null) {
                            commentViewModel.createComment {
                                textFieldValue = textFieldValue.copy(text = "")
                            }

                        } else {
                            commentViewModel.createSubComment {
                                textFieldValue = textFieldValue.copy(text = "")
                            }
                        }
                    }
                },
                placeholder = if (textFieldValue.text.isNotEmpty()) "" else {
                    if (commentState.replyComment == null) "请输入您的评论"
                    else "回复${commentState.replyComment.nickname}："
                },
                atUsers = commentState.atUserSearchResult,
                selectUser = {
                    addAtUser(it)
                    atStart.value = -1
                    inputAt.value = ""
                },
                tapAt = {
                    val b = textFieldValue.text.substring(0, textFieldValue.selection.start)
                    val a = textFieldValue.text.substring(
                        TextRange(
                            textFieldValue.selection.start,
                            textFieldValue.text.length
                        )
                    )
                    val t = "$b@$a"
                    focusRequester.requestFocus()
                    textFieldValue = textFieldValue.copy(
                        text = t,
                        selection = TextRange(textFieldValue.selection.start + 1)
                    )
                    atStart.value = textFieldValue.selection.start
                    commentViewModel.searchUser("")
                },
                selectedImage = commentState.imagePath,
                imageSelect = {
                    commentViewModel.selectImage(it)
                },
                onValueChange = {
                    commentViewModel.updateComment(it.text)
                    textFieldValue = it
                    onContentChange(it.text, it.selection.start)
                })

        }
    }
}



