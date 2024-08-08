package com.bitat.ui.common

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bitat.R
import com.bitat.viewModel.SearchTFViewModel

/**
 *    author : shilu
 *    date   : 2024/8/5  14:12
 *    desc   :
 */

@Composable
fun SearchTextField(modifier: Modifier = Modifier, result: (String) -> Unit) {
    val searchVm: SearchTFViewModel = viewModel()
    val loginState by searchVm.searchTFState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current
    Row(verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = modifier.clip(CircleShape).fillMaxWidth().border(width = 1.dp,
            color = colorResource(R.color.search_border),
            shape = CircleShape)) {
        val searchIcon = Icons.Filled.Search
        Icon(modifier = Modifier.padding(5.dp).size(30.dp),
            imageVector = searchIcon,
            contentDescription = "Search")
        BasicTextField(value = loginState.searchText,
            onValueChange = { searchVm.onTextChange(it) },
            modifier = Modifier.weight(1f).height(35.dp).padding(top = 5.dp, bottom = 5.dp),
            textStyle = MaterialTheme.typography.body1,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { // 完成按键被点击时的回调
                keyboardController?.hide() // 隐藏软键盘
                // 这里可以执行你的逻辑
                result(loginState.searchText)
            }))
        if (loginState.isShowClose) {
            IconButton(modifier = Modifier.size(30.dp), onClick = { searchVm.closeOnClick() }) {
                val clearIcon = Icons.Filled.Close
                Icon(
                    imageVector = clearIcon,
                    contentDescription = "Clear",
                )
            }
        }
    }
}