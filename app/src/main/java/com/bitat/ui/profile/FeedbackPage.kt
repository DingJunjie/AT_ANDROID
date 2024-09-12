package com.bitat.ui.profile

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bitat.Local
import com.bitat.R
import com.bitat.ext.cdp
import com.bitat.repository.singleChat.TcpClient
import com.bitat.repository.store.TokenStore
import com.bitat.repository.store.UserStore
import com.bitat.ui.common.ImagePicker
import com.bitat.ui.common.ImagePickerOption.ImageOnly
import com.bitat.ui.component.CommonTopBar
import com.bitat.ui.publish.PictureBox
import com.bitat.ui.theme.hintTextColor
import com.bitat.ui.theme.textColor
import com.bitat.utils.FeedBackUtils
import com.bitat.utils.ReportUtils
import com.bitat.viewModel.FeedbackViewModel
import com.wordsfairy.note.ui.widgets.toast.ToastModel
import com.wordsfairy.note.ui.widgets.toast.showToast
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FeedbackPage(navHostController: NavHostController) {
    val vm = viewModel(FeedbackViewModel::class)
    val state = vm.state.collectAsState()
    val focusManager = LocalFocusManager.current
    val maxSize = 200

    LaunchedEffect(Unit) {
        vm.initFeedBack(FeedBackUtils.getFeedBackTypeList())
    }

    Scaffold(topBar = {
        CommonTopBar(
            title = stringResource(id = R.string.setting_feedback),
            backFn = { navHostController.popBackStack() },
            paddingStatus = true
        )
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(10.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.setting_feedback_type),
                    style = MaterialTheme.typography.bodyLarge
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.medium_space))
                )
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3), // 设置固定列数为2
                    verticalArrangement = Arrangement.spacedBy(10.dp), // 设置行间距
                    horizontalArrangement = Arrangement.spacedBy(10.dp) // 设置列间距
                ) {
                    items(state.value.feedbackType.size) { index ->
                        val item = state.value.feedbackType[index]
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp)
                                .clip(CircleShape)
                                .background(
                                    color = if (item.isSelect) MaterialTheme.colorScheme.primary else colorResource(
                                        R.color.pop_content_bg
                                    )
                                )
                                .clickable {
                                    vm.selectFeedBack(item)
                                },
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = item.name,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontSize = 12.sp,
                                    color = if (item.isSelect) MaterialTheme.colorScheme.onPrimary else MaterialTheme.typography.bodyMedium.color
                                )
                            )
//                    }
                        }
                    }
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.medium_space))
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(id = R.string.setting_feedback_content),
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )

                    Text(
                        text = "${state.value.currentSize}/$maxSize",
                        style = MaterialTheme.typography.bodySmall.copy(color = hintTextColor),
                        modifier = Modifier.align(Alignment.CenterEnd)
                    )
                }
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.medium_space))
                )
                TextField(
                    value = state.value.feedbackContent,
                    onValueChange = {
                        if (it.length <= maxSize) {
                            vm.setContent(it)
                        }
                    },
                    placeholder = {
                        Text(
                            style = MaterialTheme.typography.bodySmall,
                            text = "请详细描述您遇到的问题、您的建议和诉求···",
                        )
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }) { },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    textStyle = MaterialTheme.typography.bodyMedium,
                    colors =
                    TextFieldDefaults.colors(
                        unfocusedContainerColor = MaterialTheme.colorScheme.background,
                        focusedContainerColor = MaterialTheme.colorScheme.background
                    )
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(dimensionResource(id = R.dimen.medium_space))
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp) // 设置列间距

                ) {
                    items(state.value.imgs) { pic ->
                        PictureBox(uri = pic, tapFn = {
//                            selectUri(pic)
                        })
                    }
                    item {
                        ImageSelect() {
                            if (it.isNotEmpty()){
                                vm.selectImage(it)
                            }
                        }
                    }
                }

            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(30.dp)
                    .height(dimensionResource(id = R.dimen.button_height))
                    .align(Alignment.BottomCenter), onClick = {
                    vm.feedback(
                        content = state.value.feedbackContent,
                        kind = state.value.selectKind
                    ) { result ->
                        if (result) {
                            ToastModel("提交成功！", ToastModel.Type.Success).showToast()
                            navHostController.popBackStack()
                        } else {
                            ToastModel("提交失败！", ToastModel.Type.Error).showToast()
                        }

                    }
                }, enabled = state.value.canClick
            ) {

                Text(
                    if (state.value.canClick) stringResource(id = R.string.setting_feedback_submit) else stringResource(
                        id = R.string.setting_feedback_submit_load
                    )
                )
            }

        }

    }
}

@Composable
fun ImageSelect(imgFn: (List<Uri>) -> Unit) {
    ImagePicker(20, ImageOnly, onSelected = {
        imgFn(it)
    }) {
        Surface(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .width(80.dp)
                .height(100.dp)
                .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
        ) {
            Icon(Icons.Filled.Add, contentDescription = "", modifier = Modifier.size(10.dp))
        }
    }
}


