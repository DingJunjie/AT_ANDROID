package com.bitat.ui.blog

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.repository.consts.Followable
import com.bitat.repository.consts.HTTP_FAIL
import com.bitat.repository.consts.HTTP_SUCCESS
import com.bitat.repository.consts.Visibility
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.repository.store.UserStore
import com.bitat.router.AtNavigation
import com.bitat.ui.component.Popup
import com.bitat.utils.EmptyArray
import com.bitat.viewModel.BlogMoreViewModel
import com.bitat.viewModel.BlogViewModel
import com.wordsfairy.note.ui.widgets.toast.ToastModel
import com.wordsfairy.note.ui.widgets.toast.showToast
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 *    author : shilu
 *    date   : 2024/8/19  11:49
 *    desc   :
 */

@SuppressLint("UnrememberedMutableState")
@Composable
fun BlogMorePop(visible: Boolean, blog: BlogBaseDto, navController: NavHostController, viewModelProvider: ViewModelProvider, onClose: () -> Unit) {

    val vm = viewModelProvider[BlogMoreViewModel::class]
    val state by vm.state.collectAsState()

    val blogVm = viewModelProvider[BlogViewModel::class]
    val blogState by blogVm.blogState.collectAsState()


    LaunchedEffect(Unit) {
        blogState.currentBlog?.let { blog ->
            vm.setUser(blog.userId)
        }

    }

    val ctx = LocalContext.current

    LaunchedEffect(state) {
        snapshotFlow { state }.distinctUntilChanged().collect {
            if (state.masking == HTTP_SUCCESS) {
                showOptResult(true, ctx, vm, onClose)
            } else if (state.masking == HTTP_FAIL) {
                showOptResult(false, ctx, vm, onClose)
            }
            if (state.notInterested == HTTP_SUCCESS) {
                showOptResult(true, ctx, vm, onClose)
            } else if (state.notInterested == HTTP_FAIL) {
                showOptResult(false, ctx, vm, onClose)
            }

            if (state.deleteResp == HTTP_SUCCESS) {
                showOptResult(true, ctx, vm, onClose)
            } else if (state.deleteResp == HTTP_FAIL) {
                showOptResult(false, ctx, vm, onClose)
            }
            if (state.authResp == HTTP_SUCCESS) {
                showOptResult(true, ctx, vm, onClose)
            } else if (state.authResp == HTTP_FAIL) {
                showOptResult(false, ctx, vm, onClose)
            }

            if (state.dtAuthResp == HTTP_SUCCESS) {
                showOptResult(true, ctx, vm, onClose)
            } else if (state.dtAuthResp == HTTP_FAIL) {
                showOptResult(false, ctx, vm, onClose)
            } // 判断是否为自己发的博文
            vm.isOther(UserStore.userInfo.id != blog.userId.toLong())
        }
    }


    Popup(visible = visible, onClose = onClose) {
        Column {
            Text(modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp,
                    fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(30.dp))
            Text(modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.blog_more_title),
                style = MaterialTheme.typography.bodyLarge.copy(fontSize = 14.sp,
                    fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(20.dp))
            Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                .background(color = colorResource(R.color.pop_content_bg)).padding(20.dp)) {
                Row(horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {

                        if (state.isOther) {
                            if (!blog.labels.contentEquals(EmptyArray.int)) {
                                vm.notInterested(blog.labels)
                                onClose()
                            } else {
                                ToastModel(ctx.getString(R.string.operation_success),
                                    ToastModel.Type.Success).showToast()
                                onClose()
                            }
                        } else {
                            vm.deleteBlog(blog.id, blog.kind)
                        }
                    }) {
                    Icon(imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(end = 10.dp))
                    Text(modifier = Modifier.fillMaxWidth(),
                        text = if (state.isOther) stringResource(R.string.blog_not_interested) else stringResource(
                            R.string.blog_delete))
                }

                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { //                        TODO()
                    }) {
                    Icon(imageVector = Icons.Outlined.Person,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(end = 10.dp))
                    Text(modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.blog_qrcode))
                }

            }
            Spacer(modifier = Modifier.height(20.dp))
            Column(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
                .background(color = colorResource(R.color.pop_content_bg)).padding(20.dp)) {

                Row(horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        if (state.isOther) {
                            AtNavigation(navController).navigateToReportUserPage()
                        } else vm.authShow(!state.isAuthShow)
                    }) {
                    Icon(imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(end = 10.dp))
                    Text(modifier = Modifier.fillMaxWidth(),
                        text = if (state.isOther) stringResource(R.string.blog_report) else stringResource(
                            R.string.blog_auth))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        if (state.isOther) vm.masking()
                        else vm.dtAuthShow(!state.isDtAuthShow)
                    }) {
                    Icon(imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(end = 10.dp))
                    Text(modifier = Modifier.fillMaxWidth(),
                        text = if (state.isOther) stringResource(R.string.blog_masking) else stringResource(
                            R.string.blog_dongtai_auth))
                }
            }
            if (state.isAuthShow) BlogVisibilePop(Visibility.getVisibility(blog.visible),
                state.isAuthShow,
                onSelect = {
                    vm.authBlog(blog.id, it.toCode())
                },
                onClose = {
                    vm.authShow(false)
                })

            if (state.isDtAuthShow) {
                BlogDtAuth(state.isDtAuthShow,
                    Followable.getFollowable(blog.albumId.toLong()),
                    setFollowFn = {
                        vm.dtAuthBlog(blogId = blog.id, albumOps = it.toCode(), cover = blog.cover)
                    },
                    onClose = { vm.dtAuthShow(false) })
            }
        }

    }
}

fun showOptResult(isSuccess: Boolean, ctx: Context, vm: BlogMoreViewModel, onClose: () -> Unit) {
    if (isSuccess) {
        ToastModel(ctx.getString(R.string.operation_success), ToastModel.Type.Success).showToast()
    } else {
        ToastModel(ctx.getString(R.string.operation_failed), ToastModel.Type.Error).showToast()
    }
    vm.stateReset()
    onClose()
}


@Composable
fun BlogVisibilePop(currentVisibility: Visibility, visible: Boolean, onSelect: (Visibility) -> Unit, onClose: () -> Unit) {
    Popup(visible = visible, onClose = { onClose() }) {
        LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
            items(Visibility.entries.size) {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 20.dp)
                    .clickable {
                        onSelect(Visibility.entries[it])
                    }) {
                    Text(modifier = Modifier.padding(end = 40.dp).align(Alignment.CenterStart),
                        text = Visibility.getUiVisibility(visibility = Visibility.entries[it]),
                        fontWeight = if (currentVisibility == Visibility.entries[it]) FontWeight.Bold else FontWeight.Normal)
                    Icon(imageVector = Icons.Outlined.Favorite,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(start = 10.dp)
                            .align(Alignment.CenterEnd))
                }
            }
        }
    }
}

@Composable
fun BlogDtAuth(visible: Boolean, currentFollowable: Followable, setFollowFn: (Followable) -> Unit, onClose: () -> Unit) {
    Popup(visible = visible, onClose = { onClose() }) {
        LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
            items(Followable.entries.size) {
                Box(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp, horizontal = 20.dp)
                    .clickable {
                        setFollowFn(Followable.entries[it])
                    }) {
                    Text(Followable.getUiFollowable(followable = Followable.entries[it]),
                        fontWeight = if (currentFollowable == Followable.entries[it]) FontWeight.Bold else FontWeight.Normal)

                    Icon(imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(start = 10.dp)
                            .align(Alignment.CenterEnd))
                }
            }
        }
    }
}