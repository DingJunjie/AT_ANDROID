package com.bitat.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.bitat.ext.toAmountUnit

@Composable
fun SocialData(
    likes: Int,
    follows: Int = -999,
    fans: Int,
    tapLike: () -> Unit = {},
    tapFans: () -> Unit = {},
    tapFollows: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        SocialDataItem(likes, "获赞", tapFn = {
            tapLike()
        })
//        VerticalDivider(
//            modifier = Modifier
//                .height(40.dp)
//                .padding(vertical = 10.dp), color = Color(0xffeeeeee)
//        )
        Spacer(modifier = Modifier.height(40.dp).width(10.dp))
        SocialDataItem(fans, "粉丝") {
            tapFans()
        }
//        VerticalDivider(
//            modifier = Modifier
//                .height(40.dp)
//                .padding(top = 10.dp), color = Color(0xffeeeeee)
//        )
        Spacer(modifier = Modifier.height(40.dp).width(10.dp))
        if (follows > -1) SocialDataItem(follows, "关注") {
            tapFollows()
        }
    }
}

@Composable
fun SocialDataItem(amount: Int, title: String, tapFn: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable {
            tapFn()
        }
    ) {
        Text(text = amount.toAmountUnit(1), fontSize = 20.sp, fontWeight = FontWeight(600))
        Text(title, fontSize = 14.sp, color = Color.Gray)
    }
}