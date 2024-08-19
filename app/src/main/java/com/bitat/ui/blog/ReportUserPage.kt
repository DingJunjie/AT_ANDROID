package com.bitat.ui.blog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.bitat.R
import com.bitat.utils.ReportUtils

/**
 *    author : shilu
 *    date   : 2024/8/19  17:43
 *    desc   :
 */

@Composable
fun ReportUserPage(navHostController: NavHostController) {
    Scaffold(topBar = {
        TopBar(title = stringResource(id = R.string.blog_report)) {

        }
    }) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().background(color = Color.White)
            .padding(innerPadding)) {
            Text(text = stringResource(R.string.blog_report_hint))
            LazyVerticalGrid(columns = GridCells.Fixed(2), // 设置固定列数为2
                modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), // 设置内边距
                verticalArrangement = Arrangement.spacedBy(16.dp), // 设置行间距
                horizontalArrangement = Arrangement.spacedBy(16.dp) // 设置列间距
            ) {
                items(ReportUtils.getReportTypeList().size) { index ->
                    TextButton(modifier = Modifier.fillMaxWidth().padding(10.dp)
                        .background(color = colorResource(R.color.pop_content_bg))
                        .clip(RoundedCornerShape(8.dp)), onClick = {

                    }) {
                        val text = ReportUtils.getReportTypeList().get(index)
                        Text(text + "")
                    }

                }
            }

            TextButton(onClick = {}) {
                Text(text = stringResource(R.string.submit))
            }
        }
    }
}

