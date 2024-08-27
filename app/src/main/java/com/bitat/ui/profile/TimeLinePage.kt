package com.bitat.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bitat.utils.ScreenUtils

/**
 *    author : shilu
 *    date   : 2024/8/27  17:47
 *    desc   :
 */

@Composable
fun TimeLinePage() {
    Column(modifier = Modifier.fillMaxSize().height(ScreenUtils.screenHeight.dp)) {
        Text("时间线")
        LazyColumn(modifier = Modifier.fillMaxWidth()) {


        }
    }
}