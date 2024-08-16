package com.bitat.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.bitat.ui.theme.subTextColor
import com.bitat.utils.TimeUtils

//@Composable
//fun UserInfo() {
//    Row(modifier = Modifier.fillMaxWidth(),
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.SpaceBetween) {
//        Avatar(url = "https://pic3.zhimg.com/v2-9041577bc5535d6abd5ddc3932f2a30e_r.jpg")
//        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp)) {
//            Text("去有风的地方")
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                Text("2024-7-17 0:16", style = Typography.bodyMedium)
//                Text("IP:贵州", style = Typography.bodyMedium)
//            }
//        }
//        TextButton(onClick = { /*TODO*/ },
//            modifier = Modifier.padding(horizontal = 5.dp, vertical = 3.dp)
//                .background(Color.Yellow)) {
//            Text("关注", color = Color.Black)
//        }
//    }
//}

/*****
 * 用户名的显示 和发布时间的显示
 */
@Composable
fun UserInfo(username: String, createTime: Long = 0, isShowTime: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically  // 垂直居中
    ) {
        Text(text = username,
            style = Typography.bodyLarge.copy(fontSize = 14.sp, //                fontWeight = FontWeight.Bold,
                color = Color.Blue, letterSpacing = 1.sp, textAlign = TextAlign.Left))

        Row(modifier = Modifier.fillMaxWidth()
            .padding(start = 5.dp, top = 5.dp, end = 4.dp, bottom = 5.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically  // 垂直居中
        ) {
            if (isShowTime) Text(text = TimeUtils.timeToText(createTime), //                                    +TimeUtils.timestampToString(createTime),
                //                            fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = Color.Black,
                textAlign = TextAlign.Left,
                modifier = Modifier.padding(start = 5.dp, top = 5.dp, end = 5.dp, bottom = 5.dp),
                fontSize = FontStyle.contentLargeSize)
            SvgIcon(path = "svg/verticalline.svg",
                tint = Color.Black,
                contentDescription = "",
                modifier = Modifier.size(20.dp))
        }
    }
}

@Composable
fun UserInfoWithAvatar(nickname: String, avatar: String, createTime: Long = 0, isShowTime: Boolean = false) {
    Row(modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically  // 垂直居中
    ) {
        Avatar(url = avatar, size = 30)
        Text(modifier = Modifier.padding(start = 10.dp),
            text = nickname,
            style = Typography.bodyLarge.copy(fontSize = 14.sp,
                color = Color.Black, //                letterSpacing = 1.sp,
                textAlign = TextAlign.Left))

        Row(modifier = Modifier.fillMaxWidth()
            .padding(start = 5.dp, top = 5.dp, end = 4.dp, bottom = 5.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically  // 垂直居中
        ) {
            if (isShowTime) Text(text = TimeUtils.timeToText(createTime), //                                    +TimeUtils.timestampToString(createTime),
                //                            fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = Color.Black,
                textAlign = TextAlign.Left,
                modifier = Modifier.padding(start = 5.dp, top = 5.dp, end = 5.dp, bottom = 5.dp),
                fontSize = FontStyle.contentLargeSize)
            SvgIcon(path = "svg/verticalline.svg",
                tint = Color.Black,
                contentDescription = "",
                modifier = Modifier.size(16.dp))

        }
    }
}

@Composable
fun UserInfoWithAddr(nickname: String, avatar: String, createTime: Long = 0, address: String, isShowTime: Boolean = false, modifier: Modifier = Modifier) {
    Row(modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically  // 垂直居中
    ) {
        Avatar(url = avatar, size = 50)
        Column(modifier = Modifier, horizontalAlignment = Alignment.Start) {
            Text(modifier = modifier.padding(start = 10.dp),
                text = nickname,
                style = Typography.bodyLarge.copy(fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    color = Color.Black))

            Row(modifier = Modifier.padding(start = 5.dp, top = 5.dp, end = 4.dp, bottom = 5.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically  // 垂直居中
            ) {
                if (isShowTime) {
                    Text(
                        text = TimeUtils.timeToText(createTime), //                                    +TimeUtils.timestampToString(createTime),
                        //                            fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        color = subTextColor,
                        textAlign = TextAlign.Left,
                        modifier = Modifier.padding(start = 5.dp,
                            top = 5.dp,
                            end = 5.dp,
                            bottom = 5.dp), //                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                    )
                }

                Text(
                    text = address,
                    letterSpacing = 1.sp,
                    color = subTextColor,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.padding(start = 5.dp,
                        top = 5.dp,
                        end = 5.dp,
                        bottom = 5.dp), //                    fontSize = MaterialTheme.typography.bodySmall.fontSize
                )
            }
        }
    }
}
