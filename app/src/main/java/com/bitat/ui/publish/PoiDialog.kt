package com.bitat.ui.publish

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.amap.api.services.core.PoiItemV2
import com.bitat.R
import com.bitat.log.CuLog
import com.bitat.log.CuTag
import com.bitat.ui.common.SearchTextField
import com.bitat.utils.doSearchQuery
import com.bitat.viewModel.PoiDLViewModedl

/**
 *    author : shilu
 *    date   : 2024/8/5  09:39
 *    desc   : 高德poi 搜索功能Dialog
 *
 *
 *    PoiDialog(onDismissRequest = {}, onSelect = {
 *                     CuLog.debug(CuTag.Publish, it.toString())
 *                 })
 *
 */

@Composable
fun PoiDialog(onDismissRequest: () -> Unit, onSelect: (PoiItemV2) -> Unit) {
    val viewModel: PoiDLViewModedl = viewModel()
    val poiState by viewModel.poiDLState.collectAsState()
    val ctx = LocalContext.current

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(modifier = Modifier.height(300.dp).fillMaxWidth()) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text(text = "附近地址",
                    modifier = Modifier.height(30.dp).fillMaxWidth()
                        .wrapContentHeight(align = Alignment.CenterVertically)  //设置竖直居中
                        .wrapContentWidth(align = Alignment.CenterHorizontally)) //设置水平居中

                SearchTextField(modifier = Modifier.padding(bottom = 10.dp), result = {
                    doSearchQuery(ctx, result = { poiList ->
                        viewModel.searchListUpdate(poiList.toMutableList())
                    })
                })
                LazyColumn {
                    CuLog.debug(CuTag.Publish, "更新poiList${poiState.poiList}")
                    items(poiState.poiList) { item ->
                        Column(modifier = Modifier.clickable(onClick = { onSelect(item) })) {
                            Row {
                                Text(text = item.title,
                                    modifier = Modifier.fillMaxWidth().height(40.dp)
                                        .wrapContentHeight(align = Alignment.CenterVertically))

                                //                          Text(text = item.dis)
                            }
                            Spacer(modifier = Modifier.height(1.dp))
                        }

                    }

                }

            }
        }
    }
}