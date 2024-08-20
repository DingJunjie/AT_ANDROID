package com.bitat.ui.blog

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.dto.resp.BlogBaseDto
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.router.AtNavigation
import com.bitat.ui.common.rememberToastState
import com.bitat.utils.EmptyArray
import com.bitat.viewModel.BlogMoreViewModel
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
fun BlogMorePop(visible: Boolean, blog: BlogBaseDto, navController: NavHostController, onClose: () -> Unit) {
    val vm: BlogMoreViewModel = viewModel()
    val state by vm.state.collectAsState()
    val toast = rememberToastState()
    val ctx = LocalContext.current

    LaunchedEffect(state) {
        snapshotFlow { state }.distinctUntilChanged().collect {
            if (state.masking) {
                ToastModel("拉黑成功！", ToastModel.Type.Success).showToast()
                onClose()
            }
            if (state.notInterested) {
                ToastModel("操作成功！", ToastModel.Type.Success).showToast()
                onClose()
            }
        }
    }
    com.bitat.ui.component.Popup(visible = visible, onClose = onClose) {
        Column {
            Text(modifier = Modifier.fillMaxWidth(),
                text = "艾特",
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

                        if (!blog.labels.contentEquals(EmptyArray.int)) {
                            vm.notInterested(blog.labels)
                            toast.show(ctx.getString(R.string.operation_success))
                            onClose()
                        } else {
                            toast.show(ctx.getString(R.string.operation_success))
                            onClose()
                        }

                    }) {
                    Icon(imageVector = Icons.Outlined.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(end = 10.dp))
                    Text(modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.blog_not_interested))
                }

                Spacer(modifier = Modifier.height(20.dp))
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
                        AtNavigation(navController).navigateToReportUserPage()
                    }) {
                    Icon(imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(end = 10.dp))
                    Text(modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.blog_report))
                }
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable {
                        vm.masking(blog.userId.toLong())
                    }) {
                    Icon(imageVector = Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        modifier = Modifier.size(30.dp).padding(end = 10.dp))
                    Text(modifier = Modifier.fillMaxWidth(),
                        text = stringResource(R.string.blog_masking))
                }
            }
        }
    }
}