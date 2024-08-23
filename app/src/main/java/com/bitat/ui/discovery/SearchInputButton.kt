package com.bitat.ui.discovery

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bitat.router.NavigationItem
import com.bitat.ui.component.BackButton

@Composable
fun SearchInputButton(
    navHostController: NavHostController,
    editable: Boolean = false,
    keyword: String = "",
    updateKeyword: (String) -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navHostController.navigate(NavigationItem.Search.route)
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xffeeeeee))
                .height(40.dp)
                .padding(vertical = 4.dp, horizontal = 6.dp),
        ) {
            Icon(Icons.Filled.Search, contentDescription = "")
            if (!editable) {
                Text("输入您想要搜索的内容", color = Color.Gray, fontSize = 12.sp)
            } else {
                SearchInput(keyword, updateKeyword)
            }
        }
    }
}

@Composable
fun SearchInput(keyword: String, updateKeyword: (String) -> Unit) {
    BasicTextField(
        value = keyword,
        onValueChange = { updateKeyword(it) },
        Modifier
            .padding(top = 5.dp, start = 10.dp)
            .fillMaxWidth()
            .height(60.dp),
        singleLine = true,
//                label = { Text() }
//                placeholder = { Text("输入您想要搜索的内容") }
    )
}

@Composable
fun SearchTopBar(
    navHostController: NavHostController,
    keyword: String,
    updateKeyword: (String) -> Unit, searchTapFn: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.height(40.dp)) {
        BackButton {
            navHostController.popBackStack()
        }
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            SearchInputButton(
                navHostController,
                editable = true,
                keyword = keyword,
                updateKeyword = updateKeyword
            )
        }
        TextButton(onClick = { searchTapFn() }) {
            Text("搜索")
        }
    }
}