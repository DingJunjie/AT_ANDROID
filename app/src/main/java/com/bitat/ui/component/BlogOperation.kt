package com.bitat.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bitat.dto.resp.BlogBaseDto
import com.bitat.ext.toAmountUnit

@Composable
fun BlogOperation(
    blog: BlogBaseDto,
    tapComment: () -> Unit = {},
    tapAt: () -> Unit = {},
    tapLike: () -> Unit = {},
    tapCollect: () -> Unit = {}
) {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp)
            .padding(end = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.padding(start = 10.dp, end = 10.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CommentButton(
                blog.comments.toString(),
                modifier = Modifier.padding(end = 10.dp)
            ) {
                tapComment()
            }

            AtButton(
                blog.ats.toString(),
                modifier = Modifier.padding(end = 10.dp)) {
                tapAt()
            }

            LikeButton(
                blog.id,
                blog.agrees.toString(),
                isLiked = blog.hasPraise,
                modifier = Modifier.padding(end = 10.dp)
            ) {
                tapLike()
            }
        }

        CollectButton(modifier = Modifier.size(20.dp)) {
            tapCollect()
        }
    }


}