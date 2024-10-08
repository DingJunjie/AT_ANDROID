package com.bitat.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitat.ext.cdp
import com.bitat.ext.csp
import com.bitat.style.FontStyle
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.theme.Typography
import com.bitat.ui.theme.hintTextColor
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
fun UserInfo(username: String, createTime: Long = 0, isShowTime: Boolean = false, moreClick: () -> Unit = {}) {
    Row(modifier = Modifier.fillMaxWidth().fillMaxHeight(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically  // 垂直居中
    ) {
        Text(text = username,
            style = Typography.bodyMedium.copy(fontSize = 32.csp, //                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp, textAlign = TextAlign.Left))

        Row(modifier = Modifier.fillMaxWidth().padding(start = 5.dp, top = 5.dp, bottom = 5.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically  // 垂直居中
        ) {
            if (isShowTime) Text(text = TimeUtils.timeToText(createTime), //                                    +TimeUtils.timestampToString(createTime),
                //                            fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = hintTextColor,
                textAlign = TextAlign.Left,
                modifier = Modifier.padding(start = 5.dp, top = 5.dp, end = 5.dp, bottom = 5.dp),
                fontSize = FontStyle.contentLargeSize)

            moreIcon(onClick = moreClick)

            Spacer(modifier = Modifier.width(30.cdp))
        }
    }
}

@Composable
fun UserInfoWithAvatar(modifier: Modifier = Modifier.fillMaxWidth().fillMaxHeight()
    .padding(vertical = 10.dp), nickname: String, avatar: String, createTime: Long = 0, isShowTime: Boolean = false, textStyle: TextStyle = Typography.bodyLarge.copy(
    fontSize = 14.sp,
    color = Color.Black, //                letterSpacing = 1.sp,
    textAlign = TextAlign.Left), isShowMore: Boolean = true, avatarSize: Int = 30) {
    Row(modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically  // 垂直居中
    ) {
        Avatar(url = avatar, size = avatarSize)
        Text(modifier = Modifier.padding(start = 10.dp), text = nickname, style = textStyle)

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
            if (isShowMore) SvgIcon(path = "svg/verticalline.svg",
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
        Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.Start, verticalArrangement = Arrangement.Center) {
            Text(modifier = modifier,
                text = nickname,
                style = Typography.bodyLarge)

            Row(modifier = Modifier.padding( top = 5.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically  // 垂直居中
            ) {
                if (isShowTime) {
                    Text(text = TimeUtils.timeToText(createTime,
                        true), //                                    +TimeUtils.timestampToString(createTime),
                        //                            fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        textAlign = TextAlign.Left,
                        modifier = Modifier.padding(
                            end = 5.dp), //                        fontSize = MaterialTheme.typography.bodySmall.fontSize
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = subTextColor,
                            fontSize = 22.csp
                        ))
                }

                Text(text = "ip:$address",
                    letterSpacing = 1.sp,
                    textAlign = TextAlign.Left,
                    modifier = Modifier,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = subTextColor,
                        fontSize = 22.csp
                    ))
            }
        }
    }
}


@Composable
fun moreIcon(onClick: () -> Unit) {
    SvgIcon(path = "svg/more.svg",
        tint = Color.Black,
        contentDescription = "",
        modifier = Modifier.size(30.cdp).clickable {
            onClick()
        })
}