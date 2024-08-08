package com.bitat.ui.blog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitat.style.FontStyle
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.theme.Typography
import com.bitat.utils.TimeUtils

/*****
 * 用户名的显示 和发布时间的显示
 */
@Composable
fun UserInfo(username: String, createTime: Long = 0, isShowTime: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically  // 垂直居中
    ) {
        Text(
            text = username,
            style = Typography.bodyLarge.copy(
                fontWeight = FontWeight.Bold,
                color = Color.Blue,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Left
            )
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp, top = 5.dp, end = 4.dp, bottom = 5.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically  // 垂直居中
        ) {
            if (isShowTime) Text(
                text = TimeUtils.timeToText(createTime),
//                                    +TimeUtils.timestampToString(createTime),
//                            fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = Color.Black,
                textAlign = TextAlign.Left,
                modifier = Modifier
                    .padding(start = 5.dp, top = 5.dp, end = 5.dp, bottom = 5.dp),
                fontSize = FontStyle.contentLargeSize
            )
            SvgIcon(
                path = "svg/verticalline.svg",
                tint = Color.Black,
                contentDescription = "",
                modifier = Modifier.size(20.dp)
            )

        }
    }


}
