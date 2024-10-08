package com.bitat.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.unit.dp
import com.bitat.R
import com.bitat.repository.dto.resp.BlogBaseDto
import com.bitat.ui.common.LottieBox

@Composable
fun BlogOperation(blog: BlogBaseDto, tapComment: () -> Unit = {}, tapAt: () -> Unit = {}, tapLike: () -> Unit = {}, tapCollect: (Int) -> Unit = {}, updateFlag: Int = 0) {
    val collectY = remember {
        mutableIntStateOf(0)
    }

    Row(modifier = Modifier.fillMaxWidth()
                .height(40.dp)
        .padding(end = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically) {
        Row(modifier = Modifier.padding(end = 10.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically) {

            CommentButton(modifier = Modifier.size(21.dp),
                count = blog.comments.toInt(),
                tintColor = MaterialTheme.colorScheme.primary) {
                tapComment()
            }
            Spacer(modifier = Modifier.width(20.dp))

            AtButton(
                modifier = Modifier.size(22.dp), tintColor = MaterialTheme.colorScheme.primary,
                count = blog.ats.toInt(),
            ) {
                tapAt()
            }
            Spacer(modifier = Modifier.width(20.dp))
            LikeButton(modifier = Modifier.size(22.dp),
                id = blog.id,
                count = blog.agrees.toInt(),
                isLiked = blog.hasPraise,
                tintColor = Color.Black) { //刷新页面、列表状态
                tapLike()
            }

//            LottieBox(lottieRes = R.raw.like_ani, isRepeat = false, modifier = Modifier.size(100.dp))
        }

        CollectButton(
            hasCollect = blog.hasCollect,
            modifier = Modifier.size(22.dp).onGloballyPositioned {
                    collectY.intValue = it.positionInWindow().y.toInt()
                },
        ) { //            blog.hasCollect = !blog.hasCollect
            tapCollect(collectY.intValue)
        }
        if (updateFlag < 0) {
            Text(text = "")
        }
    }


}