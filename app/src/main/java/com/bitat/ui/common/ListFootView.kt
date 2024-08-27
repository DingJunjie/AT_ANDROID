package com.bitat.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bitat.repository.consts.HttpLoadState
import com.bitat.state.BlogLoad

/**
 *    author : shilu
 *    date   : 2024/8/26  17:46
 *    desc   :
 */

@Composable
fun ListFootView(isShow: Boolean, loadResp: HttpLoadState, tryAgain: () -> Unit) {
    if (isShow) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(modifier = Modifier.fillMaxSize().padding(top = 10.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (loadResp == HttpLoadState.Default) CircularProgressIndicator(modifier = Modifier.size(
                    30.dp))
                Spacer(modifier = Modifier.width(20.dp))

                Text(text = when (loadResp) {
                    HttpLoadState.NoData -> "已经到底了"
                    HttpLoadState.Success -> "加载成功"
                    HttpLoadState.Fail -> "加载失败，网络错误"
                    HttpLoadState.TimeOut -> "加载失败，网络超时!"
                    HttpLoadState.Default -> "数据加载中···"
                })

            }
            TextButton(onClick = { //                vm.loadMore() { }
                tryAgain()
            }) {
                Text(text = "再试一次")
            }


        }

    }
}