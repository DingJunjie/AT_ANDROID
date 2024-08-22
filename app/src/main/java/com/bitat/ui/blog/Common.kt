package com.bitat.ui.blog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.repository.dto.req.PhoneLoginDto
import com.bitat.repository.http.auth.LoginReq
import com.bitat.ui.theme.white5
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.errer.ErrHandler


/****
 * 一个圆的 评论 @  喜欢收藏
 */
@Composable
fun Common(blogBaseDto: BlogBaseDto) {
    LaunchedEffect(true) {
        LoginReq.phone(PhoneLoginDto().apply {
            phone = "155123"
            captcha = "1234"
        }).await().map {

        }.errMap(ErrHandler::popFn)
    }
    Row(modifier = Modifier.fillMaxWidth().height(50.dp).background(white5),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically) {

        Row(modifier = Modifier.padding(start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically) {
            SvgIcon(path = "navigationbar/bottom_msg-fill.svg",
                tint = Color.White,
                contentDescription = "")
        }
        Row(modifier = Modifier.padding(start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically) {
            SvgIcon(path = "svg/at_bold.svg", tint = Color.White, contentDescription = "")
        }
        Row(modifier = Modifier.padding(start = 20.dp, end = 20.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically) {
            SvgIcon(path = "svg/like_line.svg", tint = Color.White, contentDescription = "")
        }

        Row(modifier = Modifier.padding(start = 20.dp, end = 20.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically) {
            SvgIcon(path = "svg/collection.svg", tint = Color.White, contentDescription = "")
        }

    }


}