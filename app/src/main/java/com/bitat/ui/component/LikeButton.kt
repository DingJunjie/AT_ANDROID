package com.bitat.ui.component

import android.annotation.SuppressLint
import androidx.collection.intListOf
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitat.MainCo
import com.bitat.R
import com.bitat.ext.cdp
import com.bitat.ext.toAmountUnit
import com.bitat.helpers.LikeHelper
import com.bitat.ui.common.LottieBox
import com.bitat.ui.common.SvgIcon
import com.bitat.ui.theme.Typography
import kotlinx.coroutines.launch

@Composable
fun LikeButton(
    id: Long,
    count: Int,
    isLiked: Boolean = false,
    tintColor:Color= Color.Black,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier,
    tapFn: () -> Unit
) {
    val likeHelper = LikeHelper()
    val currentLike = remember {
        mutableStateOf(isLiked)
    }
    var tmpCount = remember {
        mutableIntStateOf(count)
    }
    Row (modifier = Modifier.clickable {
        currentLike.value = !currentLike.value
        tmpCount.intValue += if (currentLike.value) 1 else -1
        if (tmpCount.intValue < 0) tmpCount.intValue = 0
        MainCo.launch {
            likeHelper.like(id, isLike = currentLike.value, {
                likeHelper.likeById(id, currentLike.value, listOf()) {
                    tapFn()
                }
            }, {

            })
        }
//        tapFn()
    }) {
        SvgIcon(
            modifier = modifier,
            path = if (currentLike.value) "svg/like_fill.svg" else "svg/like_line.svg",
            tint = if (currentLike.value) Color.Red else tintColor,
            contentDescription = "",
        )

        if (tmpCount.intValue > 0) Text(
            tmpCount.intValue.toAmountUnit(), style = Typography.bodyMedium.copy(
                fontSize = 10.sp, color = tintColor
            )
        )

//        LottieBox(
//            lottieRes = R.raw.like_ani,
//            isRepeat = true,
//            modifier = Modifier.size(400.cdp)
//        )
    }
}