package com.bitat.ui.blog

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.utils.ReportUtils
import com.bitat.viewModel.BlogMoreViewModel
import com.wordsfairy.note.ui.widgets.toast.ToastModel
import com.wordsfairy.note.ui.widgets.toast.showToast

/**
 *    author : shilu
 *    date   : 2024/8/19  17:43
 *    desc   :
 */

@Composable
fun ReportUserPage(navHostController: NavHostController) {

    val vm: BlogMoreViewModel = viewModel()
    val state = vm.state.collectAsState()
    LaunchedEffect(state) {
        if (state.value.report) {
            ToastModel("举报成功！", ToastModel.Type.Success).showToast()
        }
    }

    LaunchedEffect(Unit) {
        vm.setReportList(ReportUtils.getReportTypeList())
    }

    Scaffold(topBar = {
        TopBar(title = stringResource(id = R.string.blog_report)) {
            navHostController.popBackStack()
        }
    }) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
                .padding(innerPadding), verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = stringResource(R.string.blog_report_hint),
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 10.dp),
                textAlign = TextAlign.Start
            )
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // 设置固定列数为2
                contentPadding = PaddingValues(16.dp), // 设置内边距
                verticalArrangement = Arrangement.spacedBy(10.dp), // 设置行间距
                horizontalArrangement = Arrangement.spacedBy(10.dp) // 设置列间距
            ) {
                items(state.value.reportList.size) { index ->
                    val item = state.value.reportList[index]
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                color = if (item.isSelect) colorResource(R.color.teal_700) else colorResource(
                                    R.color.pop_content_bg
                                )
                            )
                            .clickable {
                                item.isSelect = !item.isSelect
                                vm.selectRepor(item)
                            },
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val text = ReportUtils.getReportTypeList()[index].name
                        Text(
                            text = text,
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp)
                        )
                    }
                }
            }

            if (state.value.updateIndex < 0) {
                Text(text = state.value.updateIndex.toString())
            }

            TextButton(modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp, end = 40.dp, top = 20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ), onClick = {

//                    vm.report()
                }) {
                Text(text = stringResource(R.string.submit))
            }

        }
    }
}

