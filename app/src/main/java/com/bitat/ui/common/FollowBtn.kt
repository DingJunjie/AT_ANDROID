package com.bitat.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bitat.ext.csp
import com.bitat.repository.consts.BLACKLIST
import com.bitat.repository.consts.DEFAULT
import com.bitat.repository.consts.FOLLOWED
import com.bitat.repository.store.UserStore
import com.bitat.ui.theme.white
import com.bitat.viewModel.FollowBtnViewModel
import com.wordsfairy.note.ui.widgets.toast.ToastModel
import com.wordsfairy.note.ui.widgets.toast.showToast

/**
 *    author : shilu
 *    date   : 2024/8/16  16:11
 *    desc   :
 */
@Composable
fun FollowBtn(modifier: Modifier = Modifier, rel: Int, revRel: Int, userId: Long, clickFn: (Int) -> Unit) { //关注按钮

    var relation by remember { mutableStateOf("") }

    val vm: FollowBtnViewModel = viewModel()
    val state = vm.state.collectAsState()
    vm.initType(rel)
    Column(modifier = modifier) {
        DebouncedButton(modifier = Modifier.fillMaxSize().weight(1f).clip(CircleShape), onClick = {
            vm.followUser(rel, revRel, userId, onSuccess = { rel ->
                clickFn(rel)
                UserStore.refreshUser()
            }, onError = { error->
                when(error.code){
                    -1 -> ToastModel(error.msg, ToastModel.Type.Error,1000).showToast()
                }
            })

        }) {
            Text(text = state.value.relContent,
                style = MaterialTheme.typography.bodyMedium.copy(color = white, fontSize = 22.csp))
        }
    }

}